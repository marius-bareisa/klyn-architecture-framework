package com.klynaf.feature.watchlist.presentation

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.uicore.model.MediaTypeUi
import com.klynaf.uicore.util.ImageSize
import com.klynaf.uicore.util.toTmdbImageUrl
import javax.inject.Inject

class WatchlistUiModelMapper @Inject constructor() {
    fun map(item: WatchlistItem): WatchlistUiModel = WatchlistUiModel(
        mediaId = item.mediaId,
        mediaTypeRoute = item.mediaType.toRoute(),
        mediaTypeUi = when (item.mediaType) {
            MediaType.Movie -> MediaTypeUi.Movie
            MediaType.TvShow -> MediaTypeUi.TvShow
        },
        title = item.title,
        posterUrl = item.posterPath.toTmdbImageUrl(ImageSize.W342),
    )
}
