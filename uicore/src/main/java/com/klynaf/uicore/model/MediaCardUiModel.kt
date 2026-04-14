package com.klynaf.uicore.model

data class MediaCardUiModel(
    val uniqueId: String,
    val posterUrl: String?,
    val title: String,
    val voteAverage: Double,
    val mediaId: Int,
    val mediaTypeRoute: String,
)
