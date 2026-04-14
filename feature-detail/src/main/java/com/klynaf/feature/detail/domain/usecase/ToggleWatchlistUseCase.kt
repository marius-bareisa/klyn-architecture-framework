package com.klynaf.feature.detail.domain.usecase

import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class ToggleWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(item: WatchlistItem) = repository.toggle(item)
}
