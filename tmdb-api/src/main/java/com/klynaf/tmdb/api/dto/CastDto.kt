package com.klynaf.tmdb.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CastDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "character") val character: String,
    @param:Json(name = "profile_path") val profilePath: String?,
)

@JsonClass(generateAdapter = true)
data class CreditsResponse(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "cast") val cast: List<CastDto>,
)
