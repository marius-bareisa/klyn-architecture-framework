package com.klynaf.core.domain.source.local

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

interface WatchlistLocalDataSource {
    fun getAll(): Flow<List<WatchlistItem>>
    suspend fun toggle(item: WatchlistItem)
    suspend fun remove(mediaId: Int, mediaType: MediaType)
    fun isWatchlisted(mediaId: Int, type: MediaType): Flow<Boolean>
}
