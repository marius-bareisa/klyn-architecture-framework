package com.klynaf.feature.detail.presentation

sealed interface DetailUiModel {
    val title: String
    val posterPath: String?
    val overview: String
    val releaseYear: String
    val formattedRating: String
    val backdropUrl: String?

    data class MovieDetail(
        override val title: String,
        override val posterPath: String?,
        override val overview: String,
        override val releaseYear: String,
        override val formattedRating: String,
        override val backdropUrl: String?,
    ) : DetailUiModel

    data class TvDetail(
        override val title: String,
        override val posterPath: String?,
        override val overview: String,
        override val releaseYear: String,
        override val formattedRating: String,
        override val backdropUrl: String?,
    ) : DetailUiModel
}
