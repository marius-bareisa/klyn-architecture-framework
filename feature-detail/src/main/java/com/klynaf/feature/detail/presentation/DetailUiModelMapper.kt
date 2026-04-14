package com.klynaf.feature.detail.presentation

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.model.TvShow
import com.klynaf.uicore.model.MediaCardUiModel
import com.klynaf.uicore.util.ImageSize
import com.klynaf.uicore.util.toTmdbImageUrl
import javax.inject.Inject

class DetailUiModelMapper @Inject constructor() {
    fun mapMovie(movie: Movie): DetailUiModel = DetailUiModel.MovieDetail(
        title = movie.title,
        posterPath = movie.posterPath,
        overview = movie.overview,
        releaseYear = extractYear(movie.releaseDate),
        formattedRating = "%.1f".format(movie.voteAverage),
        backdropUrl = movie.backdropPath.toTmdbImageUrl(ImageSize.W780),
    )

    fun mapTvShow(tvShow: TvShow): DetailUiModel = DetailUiModel.TvDetail(
        title = tvShow.name,
        posterPath = tvShow.posterPath,
        overview = tvShow.overview,
        releaseYear = extractYear(tvShow.firstAirDate),
        formattedRating = "%.1f".format(tvShow.voteAverage),
        backdropUrl = tvShow.backdropPath.toTmdbImageUrl(ImageSize.W780),
    )

    fun mapCast(cast: Cast): CastUiModel {
        return CastUiModel(
            id = cast.id,
            name = cast.name,
            character = cast.character,
            profileUrl = cast.profilePath.toTmdbImageUrl(ImageSize.W185),
        )
    }

    fun mapTrailer(trailer: Trailer): TrailerUiModel {
        return TrailerUiModel(
            name = trailer.name,
            thumbnailUrl = trailer.thumbnailUrl,
            videoUrl = trailer.videoUrl,
        )
    }

    fun mapMediaItem(item: MediaItem): MediaCardUiModel = when (item) {
        is MediaItem.MovieItem -> MediaCardUiModel(
            uniqueId = "movie_${item.movie.id}",
            posterUrl = item.movie.posterPath.toTmdbImageUrl(ImageSize.W342),
            title = item.movie.title,
            voteAverage = item.movie.voteAverage,
            mediaId = item.movie.id,
            mediaTypeRoute = MediaType.Movie.toRoute(),
        )

        is MediaItem.TvItem -> MediaCardUiModel(
            uniqueId = "tv_${item.tvShow.id}",
            posterUrl = item.tvShow.posterPath.toTmdbImageUrl(ImageSize.W342),
            title = item.tvShow.name,
            voteAverage = item.tvShow.voteAverage,
            mediaId = item.tvShow.id,
            mediaTypeRoute = MediaType.TvShow.toRoute(),
        )
    }

    private fun extractYear(date: String): String =
        if (date.length >= 4) date.take(4) else ""
}
