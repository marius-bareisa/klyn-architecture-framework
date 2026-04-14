package com.klynaf.feature.watchlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klynaf.core.domain.model.MediaType
import com.klynaf.feature.watchlist.domain.usecase.GetWatchlistUseCase
import com.klynaf.feature.watchlist.domain.usecase.RemoveFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    getWatchlist: GetWatchlistUseCase,
    private val removeFromWatchlist: RemoveFromWatchlistUseCase,
    private val mapper: WatchlistUiModelMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistState())
    val state: StateFlow<WatchlistState> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<WatchlistNavEvent>()
    val navEvents: SharedFlow<WatchlistNavEvent> = _navEvents.asSharedFlow()

    init {
        getWatchlist()
            .onEach { items ->
                _state.update {
                    WatchlistState(items = items.map { mapper.map(it) })
                }
            }
            .launchIn(viewModelScope)
    }

    fun onItemClicked(uiModel: WatchlistUiModel) {
        viewModelScope.launch {
            _navEvents.emit(
                WatchlistNavEvent.NavigateToDetail(
                    uiModel.mediaId,
                    uiModel.mediaTypeRoute
                )
            )
        }
    }

    fun onItemRemoved(uiModel: WatchlistUiModel) {
        viewModelScope.launch {
            removeFromWatchlist(
                mediaId = uiModel.mediaId,
                mediaType = MediaType.fromRoute(uiModel.mediaTypeRoute)
            )
        }
    }
}
