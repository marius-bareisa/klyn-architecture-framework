package com.klynaf.feature.home.presentation

sealed interface HomeNavEvent {
    data class NavigateToDetail(val mediaId: Int, val mediaType: String) : HomeNavEvent
}
