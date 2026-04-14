package com.klynaf.feature.detail.domain.usecase

import javax.inject.Inject

data class DetailUseCases @Inject constructor(
    val getMediaDetail: GetMediaDetailUseCase,
    val getCredits: GetCreditsUseCase,
    val getTrailers: GetTrailersUseCase,
    val getSimilar: GetSimilarUseCase,
    val getWatchlistStatus: GetWatchlistStatusUseCase,
    val toggleWatchlist: ToggleWatchlistUseCase,
)
