package com.klynaf.feature.search.presentation

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.MediaType
import com.klynaf.uicore.model.MediaTypeUi
import com.klynaf.uicore.util.ImageSize
import com.klynaf.uicore.util.toTmdbImageUrl
import javax.inject.Inject

class SearchResultUiModelMapper @Inject constructor() {

    fun map(item: MediaItem): SearchResultUiModel = when (item) {
        is MediaItem.MovieItem -> SearchResultUiModel(
            uniqueId = "movie_${item.movie.id}",
            title = item.movie.title,
            posterUrl = item.movie.posterPath.toTmdbImageUrl(ImageSize.W342),
            year = extractYear(item.movie.releaseDate),
            mediaId = item.movie.id,
            mediaTypeRoute = MediaType.Movie.toRoute(),
            mediaTypeUi = MediaTypeUi.Movie,
        )
        is MediaItem.TvItem -> SearchResultUiModel(
            uniqueId = "tv_${item.tvShow.id}",
            title = item.tvShow.name,
            posterUrl = item.tvShow.posterPath.toTmdbImageUrl(ImageSize.W342),
            year = extractYear(item.tvShow.firstAirDate),
            mediaId = item.tvShow.id,
            mediaTypeRoute = MediaType.TvShow.toRoute(),
            mediaTypeUi = MediaTypeUi.TvShow,
        )
    }

    private fun extractYear(date: String): String =
        if (date.length >= 4) date.take(4) else ""
}
