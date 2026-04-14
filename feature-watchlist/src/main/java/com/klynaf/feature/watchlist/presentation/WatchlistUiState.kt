package com.klynaf.feature.watchlist.presentation

import com.klynaf.uicore.model.MediaTypeUi

data class WatchlistUiModel(
    val mediaId: Int,
    val mediaTypeRoute: String,
    val mediaTypeUi: MediaTypeUi,
    val title: String,
    val posterUrl: String?,
)

data class WatchlistState(
    val items: List<WatchlistUiModel> = emptyList(),
) {
    val isEmpty: Boolean get() = items.isEmpty()
}

sealed interface WatchlistNavEvent {
    data class NavigateToDetail(val mediaId: Int, val mediaType: String) : WatchlistNavEvent
}
