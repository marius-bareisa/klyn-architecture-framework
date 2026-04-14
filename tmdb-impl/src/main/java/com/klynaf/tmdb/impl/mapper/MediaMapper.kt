package com.klynaf.tmdb.impl.mapper

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.TvShow
import com.klynaf.tmdb.api.dto.CastDto
import com.klynaf.tmdb.api.dto.MovieDto
import com.klynaf.tmdb.api.dto.SearchResultDto
import com.klynaf.tmdb.api.dto.TvShowDto

internal fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    voteAverage = voteAverage,
    releaseDate = releaseDate
)

internal fun TvShowDto.toDomain(): TvShow = TvShow(
    id = id,
    name = name,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    voteAverage = voteAverage,
    firstAirDate = firstAirDate
)

internal fun CastDto.toDomain(): Cast = Cast(
    id = id,
    name = name,
    character = character,
    profilePath = profilePath
)

internal fun SearchResultDto.toDomainOrNull(): MediaItem? {
    return when (mediaType) {
        "movie" -> {
            if (title == null) return null
            MediaItem.MovieItem(
                Movie(
                    id = id,
                    title = title!!,
                    posterPath = posterPath,
                    backdropPath = null,
                    overview = overview ?: "",
                    voteAverage = voteAverage ?: 0.0,
                    releaseDate = releaseDate ?: ""
                )
            )
        }

        "tv" -> {
            if (name == null) return null
            MediaItem.TvItem(
                TvShow(
                    id = id,
                    name = name!!,
                    posterPath = posterPath,
                    backdropPath = null,
                    overview = overview ?: "",
                    voteAverage = voteAverage ?: 0.0,
                    firstAirDate = firstAirDate ?: ""
                )
            )
        }

        else -> null
    }
}
