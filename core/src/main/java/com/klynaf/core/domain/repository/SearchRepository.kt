package com.klynaf.core.domain.repository

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: String, page: Int = 1): Flow<Result<List<MediaItem>>>
}
