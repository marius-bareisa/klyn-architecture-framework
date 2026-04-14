package com.klynaf.feature.detail.domain.usecase

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchlistStatusUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(id: Int, mediaType: MediaType): Flow<Boolean> = repository.isWatchlisted(id, mediaType)
}
