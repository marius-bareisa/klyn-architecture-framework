package com.klynaf.feature.detail.presentation

import com.klynaf.core.domain.util.Result

fun DetailState.clearErrorsForRefresh(): DetailState = this.copy(
    isRefreshing = true,
    media = if (this.media is Result.Error) Result.Loading else this.media,
    cast = if (this.cast is Result.Error) Result.Loading else this.cast,
    trailers = if (this.trailers is Result.Error) Result.Loading else this.trailers,
    similar = if (this.similar is Result.Error) Result.Loading else this.similar,
)
