package com.klynaf.core.domain.repository

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAll(): Flow<List<WatchlistItem>>
    suspend fun toggle(item: WatchlistItem)
    suspend fun remove(mediaId: Int, mediaType: MediaType)
    fun isWatchlisted(mediaId: Int, type: MediaType): Flow<Boolean>
}
