package com.klynaf.tmdb.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PagedResponse<T>(
    @param:Json(name = "page") val page: Int,
    @param:Json(name = "results") val results: List<T>,
    @param:Json(name = "total_pages") val totalPages: Int,
    @param:Json(name = "total_results") val totalResults: Int,
)
