package com.klynaf.feature.home.presentation

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.TvShow
import com.klynaf.uicore.model.MediaCardUiModel
import com.klynaf.uicore.util.ImageSize
import com.klynaf.uicore.util.toTmdbImageUrl
import javax.inject.Inject

class HomeUiModelMapper @Inject constructor() {
    fun mapMovie(movie: Movie): MediaCardUiModel = MediaCardUiModel(
        uniqueId = "movie_${movie.id}",
        posterUrl = movie.posterPath.toTmdbImageUrl(ImageSize.W342),
        title = movie.title,
        voteAverage = movie.voteAverage,
        mediaId = movie.id,
        mediaTypeRoute = MediaType.Movie.toRoute(),
    )

    fun mapTvShow(tvShow: TvShow): MediaCardUiModel = MediaCardUiModel(
        uniqueId = "tv_${tvShow.id}",
        posterUrl = tvShow.posterPath.toTmdbImageUrl(ImageSize.W342),
        title = tvShow.name,
        voteAverage = tvShow.voteAverage,
        mediaId = tvShow.id,
        mediaTypeRoute = MediaType.TvShow.toRoute(),
    )
}
