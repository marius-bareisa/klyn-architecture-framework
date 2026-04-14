package com.klynaf.database.impl.source

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.core.domain.source.local.WatchlistLocalDataSource
import com.klynaf.moviestorage.dao.WatchlistDao
import com.klynaf.database.impl.mapper.toDomain
import com.klynaf.database.impl.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WatchlistLocalDataSourceImpl @Inject constructor(
    private val watchlistDao: WatchlistDao,
) : WatchlistLocalDataSource {
    override fun getAll(): Flow<List<WatchlistItem>> = watchlistDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun toggle(item: WatchlistItem) {
        val deleted = watchlistDao.deleteByMediaId(item.mediaId, item.mediaType)
        if (deleted == 0) {
            watchlistDao.insert(item.toEntity())
        }
    }

    override suspend fun remove(mediaId: Int, mediaType: MediaType) {
        watchlistDao.deleteByMediaId(mediaId, mediaType)
    }

    override fun isWatchlisted(mediaId: Int, type: MediaType): Flow<Boolean> =
        watchlistDao.isWatchlisted(mediaId, type)
}
