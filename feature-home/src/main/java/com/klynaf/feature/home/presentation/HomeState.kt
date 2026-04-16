package com.klynaf.feature.home.presentation

import com.klynaf.uicore.model.MediaCardUiModel

sealed interface LoadMoreState {
    data object Idle : LoadMoreState
    data object Loading : LoadMoreState
    data class Error(val throwable: Throwable) : LoadMoreState
}

sealed interface SectionState {
    data object Loading : SectionState
    data class Error(val throwable: Throwable) : SectionState
    data class Success(
        val items: List<MediaCardUiModel>,
        val page: Int,
        val loadMoreState: LoadMoreState = LoadMoreState.Idle,
        val hasReachedEnd: Boolean = false,
    ) : SectionState
}

data class HomeState(
    val trending: SectionState = SectionState.Loading,
    val popularMovies: SectionState = SectionState.Loading,
    val popularTv: SectionState = SectionState.Loading,
    val topRatedMovies: SectionState = SectionState.Loading,
    val topRatedTv: SectionState = SectionState.Loading,
    val isRefreshing: Boolean = false,
) {
    val isAnySectionLoading: Boolean
        get() = trending is SectionState.Loading ||
                popularMovies is SectionState.Loading ||
                popularTv is SectionState.Loading ||
                topRatedMovies is SectionState.Loading ||
                topRatedTv is SectionState.Loading
}

internal fun HomeState.forRefresh(): HomeState = copy(
    isRefreshing = true,
    trending = SectionState.Loading,
    popularMovies = SectionState.Loading,
    popularTv = SectionState.Loading,
    topRatedMovies = SectionState.Loading,
    topRatedTv = SectionState.Loading,
)

internal fun HomeState.sectionFor(category: HomeCategory): SectionState = when (category) {
    HomeCategory.TRENDING -> trending
    HomeCategory.POPULAR_MOVIES -> popularMovies
    HomeCategory.POPULAR_TV -> popularTv
    HomeCategory.TOP_RATED_MOVIES -> topRatedMovies
    HomeCategory.TOP_RATED_TV -> topRatedTv
}

internal fun HomeState.updateSection(
    category: HomeCategory,
    transform: (SectionState) -> SectionState,
): HomeState = when (category) {
    HomeCategory.TRENDING -> copy(trending = transform(trending))
    HomeCategory.POPULAR_MOVIES -> copy(popularMovies = transform(popularMovies))
    HomeCategory.POPULAR_TV -> copy(popularTv = transform(popularTv))
    HomeCategory.TOP_RATED_MOVIES -> copy(topRatedMovies = transform(topRatedMovies))
    HomeCategory.TOP_RATED_TV -> copy(topRatedTv = transform(topRatedTv))
}
