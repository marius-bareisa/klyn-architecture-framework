package com.klynaf.data.repository

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.repository.SearchRepository
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.source.remote.SearchRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: SearchRemoteDataSource,
) : SearchRepository {

    override fun search(query: String, page: Int): Flow<Result<List<MediaItem>>> =
        remoteDataSource.search(query, page)
}
