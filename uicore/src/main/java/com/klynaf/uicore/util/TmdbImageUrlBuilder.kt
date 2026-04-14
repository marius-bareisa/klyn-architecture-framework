package com.klynaf.uicore.util

enum class ImageSize(val value: String) {
    W185("w185"),
    W342("w342"),
    W780("w780"),
}

fun String?.toTmdbImageUrl(size: ImageSize = ImageSize.W342): String? =
    this?.let { "https://image.tmdb.org/t/p/${size.value}$it" }
