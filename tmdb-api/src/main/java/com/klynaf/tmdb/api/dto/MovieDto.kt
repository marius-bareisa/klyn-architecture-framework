package com.klynaf.tmdb.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "poster_path") val posterPath: String?,
    @param:Json(name = "backdrop_path") val backdropPath: String?,
    @param:Json(name = "overview") val overview: String,
    @param:Json(name = "vote_average") val voteAverage: Double,
    @param:Json(name = "release_date") val releaseDate: String,
)
