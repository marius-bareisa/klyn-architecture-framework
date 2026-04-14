package com.klynaf.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klynaf.core.domain.util.Result
import com.klynaf.feature.search.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val mapper: SearchResultUiModelMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<SearchNavEvent>()
    val navEvents: SharedFlow<SearchNavEvent> = _navEvents.asSharedFlow()

    private val retryTrigger = MutableSharedFlow<Unit>()

    init {
        observeQuery()
    }

    fun onQueryChanged(query: String) {
        _state.update { state ->
            if (query.length <= 1) {
                state.copy(query = query, items = emptyList(), isLoading = false, errorThrowable = null)
            } else {
                state.copy(query = query)
            }
        }
    }

    fun onQueryCleared() {
        _state.update { SearchState() }
    }

    fun onRetry() {
        viewModelScope.launch {
            retryTrigger.emit(Unit)
        }
    }

    fun onItemClicked(mediaId: Int, mediaTypeRoute: String) {
        viewModelScope.launch {
            _navEvents.emit(
                SearchNavEvent.NavigateToDetail(mediaId, mediaTypeRoute)
            )
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeQuery() {
        val queryFlow = _state.map { it.query }
            .distinctUntilChanged()

        merge(queryFlow, retryTrigger.map { _state.value.query })
            .debounce(300L)
            .filter { it.length > 1 }
            .flatMapLatest { query -> searchUseCase(query, 1) }
            .onEach { result ->
                when (result) {
                    is Result.Loading -> _state.update { it.copy(isLoading = true, errorThrowable = null) }
                    is Result.Success -> _state.update {
                        it.copy(
                            isLoading = false,
                            items = result.data.map { item -> mapper.map(item) },
                            errorThrowable = null,
                        )
                    }

                    is Result.Error -> _state.update {
                        it.copy(
                            isLoading = false,
                            errorThrowable = result.throwable,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
