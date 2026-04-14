package com.klynaf.feature.watchlist.domain.usecase

import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.core.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchlistUseCase @Inject constructor(private val repository: WatchlistRepository) {
    operator fun invoke(): Flow<List<WatchlistItem>> = repository.getAll()
}
