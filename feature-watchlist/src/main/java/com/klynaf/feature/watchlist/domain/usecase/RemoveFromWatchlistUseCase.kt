package com.klynaf.feature.watchlist.domain.usecase

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class RemoveFromWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(mediaId: Int, mediaType: MediaType) =
        repository.remove(mediaId, mediaType)
}
