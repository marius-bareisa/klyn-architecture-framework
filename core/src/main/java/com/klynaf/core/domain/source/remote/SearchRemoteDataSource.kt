package com.klynaf.core.domain.source.remote

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface SearchRemoteDataSource {
    fun search(query: String, page: Int): Flow<Result<List<MediaItem>>>
}
