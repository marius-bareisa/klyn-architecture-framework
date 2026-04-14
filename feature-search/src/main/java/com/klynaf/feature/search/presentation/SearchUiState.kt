package com.klynaf.feature.search.presentation

import com.klynaf.uicore.model.MediaTypeUi

data class SearchResultUiModel(
    val uniqueId: String,
    val title: String,
    val posterUrl: String?,
    val year: String,
    val mediaId: Int,
    val mediaTypeRoute: String,
    val mediaTypeUi: MediaTypeUi,
)

data class SearchState(
    val query: String = "",
    val items: List<SearchResultUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorThrowable: Throwable? = null,
) {
    val hasNoResults: Boolean get() = items.isEmpty() && query.length > 1 && !isLoading && errorThrowable == null
}

sealed interface SearchNavEvent {
    data class NavigateToDetail(val mediaId: Int, val mediaType: String) : SearchNavEvent
}
