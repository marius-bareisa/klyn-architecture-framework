package com.klynaf.core.domain.model

enum class MediaType(val route: String) {
    Movie("movie"),
    TvShow("tv");

    fun toRoute(): String = route

    companion object {
        fun fromRoute(route: String): MediaType =
            entries.first { it.route == route }
    }
}
