package com.klynaf.data.repository

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.core.domain.repository.WatchlistRepository
import com.klynaf.core.domain.source.local.WatchlistLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class WatchlistRepositoryImpl @Inject constructor(
    private val localDataSource: WatchlistLocalDataSource,
) : WatchlistRepository {

    override fun getAll(): Flow<List<WatchlistItem>> =
        localDataSource.getAll()

    override suspend fun toggle(item: WatchlistItem) {
        localDataSource.toggle(item)
    }

    override suspend fun remove(mediaId: Int, mediaType: MediaType) {
        localDataSource.remove(mediaId, mediaType)
    }

    override fun isWatchlisted(mediaId: Int, type: MediaType): Flow<Boolean> =
        localDataSource.isWatchlisted(mediaId, type)
}
