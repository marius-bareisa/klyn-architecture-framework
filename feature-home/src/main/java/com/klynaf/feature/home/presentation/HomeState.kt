package com.klynaf.feature.home.presentation

import com.klynaf.core.domain.util.Result
import com.klynaf.uicore.model.MediaCardUiModel

data class HomeState(
    val trending: Result<List<MediaCardUiModel>> = Result.Loading,
    val popularMovies: Result<List<MediaCardUiModel>> = Result.Loading,
    val popularTv: Result<List<MediaCardUiModel>> = Result.Loading,
    val topRatedMovies: Result<List<MediaCardUiModel>> = Result.Loading,
    val topRatedTv: Result<List<MediaCardUiModel>> = Result.Loading,
    val isRefreshing: Boolean = false,
) {
    val isAnySectionLoading: Boolean
        get() = trending is Result.Loading ||
                popularMovies is Result.Loading ||
                popularTv is Result.Loading ||
                topRatedMovies is Result.Loading ||
                topRatedTv is Result.Loading
}

internal fun HomeState.clearErrorsForRefresh(): HomeState = this.copy(
    isRefreshing = true,
    trending = if (this.trending is Result.Error) Result.Loading else this.trending,
    popularMovies = if (this.popularMovies is Result.Error) Result.Loading else this.popularMovies,
    popularTv = if (this.popularTv is Result.Error) Result.Loading else this.popularTv,
    topRatedMovies = if (this.topRatedMovies is Result.Error) Result.Loading else this.topRatedMovies,
    topRatedTv = if (this.topRatedTv is Result.Error) Result.Loading else this.topRatedTv
)
