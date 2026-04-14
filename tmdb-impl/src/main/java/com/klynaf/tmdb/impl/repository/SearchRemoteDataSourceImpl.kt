package com.klynaf.tmdb.impl.repository

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.source.remote.SearchRemoteDataSource
import com.klynaf.tmdb.api.service.TmdbSearchService
import com.klynaf.tmdb.impl.mapper.toDomainOrNull
import com.klynaf.tmdb.impl.util.fetchFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    private val searchService: TmdbSearchService,
) : SearchRemoteDataSource {

    override fun search(query: String, page: Int): Flow<Result<List<MediaItem>>> =
        fetchFlow({ searchService.searchMulti(query, page) }) { response ->
            response.results.mapNotNull { it.toDomainOrNull() }
        }
}
