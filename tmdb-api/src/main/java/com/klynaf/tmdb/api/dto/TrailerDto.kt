package com.klynaf.tmdb.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrailerDto(
    @param:Json(name = "key") val key: String,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "site") val site: String,
    @param:Json(name = "type") val type: String,
)

@JsonClass(generateAdapter = true)
data class VideosResponse(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "results") val results: List<TrailerDto>,
)
