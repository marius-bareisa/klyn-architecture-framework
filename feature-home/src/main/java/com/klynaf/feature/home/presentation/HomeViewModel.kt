package com.klynaf.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.util.mapResult
import com.klynaf.feature.home.domain.usecase.HomeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: HomeUseCases,
    private val mapper: HomeUiModelMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<HomeNavEvent>()
    val navEvents: SharedFlow<HomeNavEvent> = _navEvents.asSharedFlow()

    init {
        loadContent(categories = HomeCategory.entries, isRefresh = false)
    }

    fun onRefresh() {
        if (_state.value.isRefreshing || _state.value.isAnySectionLoading) return

        _state.update { it.clearErrorsForRefresh() }

        loadContent(categories = HomeCategory.entries, isRefresh = true)
    }

    fun onRetry(category: HomeCategory) {
        if (_state.value.isRefreshing) return
        loadContent(categories = listOf(category), isRefresh = false)
    }

    fun onItemClicked(mediaId: Int, mediaTypeRoute: String) {
        viewModelScope.launch {
            _navEvents.emit(
                HomeNavEvent.NavigateToDetail(
                    mediaId,
                    mediaTypeRoute
                )
            )
        }
    }

    private fun loadContent(categories: List<HomeCategory>, isRefresh: Boolean) {
        viewModelScope.launch {
            val jobs = categories.map { category ->
                launch {
                    when (category) {
                        HomeCategory.TRENDING -> fetchSection(
                            flow = useCases.getTrendingMovies(page = 1).map { result ->
                                result.mapResult { list -> list.map { mapper.mapMovie(it) } }
                            },
                            isRefresh = isRefresh,
                            stateUpdater = { state, res -> state.copy(trending = res) }
                        )

                        HomeCategory.POPULAR_MOVIES -> fetchSection(
                            flow = useCases.getPopularMovies().map { result ->
                                result.mapResult { list -> list.map { mapper.mapMovie(it) } }
                            },
                            isRefresh = isRefresh,
                            stateUpdater = { state, res -> state.copy(popularMovies = res) }
                        )

                        HomeCategory.POPULAR_TV -> fetchSection(
                            flow = useCases.getPopularTv().map { result ->
                                result.mapResult { list -> list.map { mapper.mapTvShow(it) } }
                            },
                            isRefresh = isRefresh,
                            stateUpdater = { state, res -> state.copy(popularTv = res) }
                        )

                        HomeCategory.TOP_RATED_MOVIES -> fetchSection(
                            flow = useCases.getTopRatedMovies().map { result ->
                                result.mapResult { list -> list.map { mapper.mapMovie(it) } }
                            },
                            isRefresh = isRefresh,
                            stateUpdater = { state, res -> state.copy(topRatedMovies = res) }
                        )

                        HomeCategory.TOP_RATED_TV -> fetchSection(
                            flow = useCases.getTopRatedTv().map { result ->
                                result.mapResult { list -> list.map { mapper.mapTvShow(it) } }
                            },
                            isRefresh = isRefresh,
                            stateUpdater = { state, res -> state.copy(topRatedTv = res) }
                        )
                    }
                }
            }

            jobs.joinAll()

            if (isRefresh) {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private suspend inline fun <T> fetchSection(
        flow: Flow<Result<T>>,
        isRefresh: Boolean,
        crossinline stateUpdater: (HomeState, Result<T>) -> HomeState
    ) {
        if (!isRefresh) {
            _state.update { stateUpdater(it, Result.Loading) }
        }

        flow.collect { result ->
            if (isRefresh && result is Result.Loading) return@collect
            _state.update { stateUpdater(it, result) }
        }
    }
}
