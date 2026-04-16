package com.klynaf.tmdb.impl.repository

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.source.remote.TvRemoteDataSource
import com.klynaf.core.domain.util.Result
import com.klynaf.tmdb.api.service.TmdbTvService
import com.klynaf.tmdb.impl.mapper.toDomain
import com.klynaf.tmdb.impl.mapper.toDomainOrNull
import com.klynaf.tmdb.impl.util.fetchFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TvRemoteDataSourceImpl @Inject constructor(
    private val tvService: TmdbTvService,
) : TvRemoteDataSource {

    override fun getPopularTv(page: Int): Flow<Result<List<TvShow>>> =
        fetchFlow({ tvService.getPopular(page) }) { it.results.map { dto -> dto.toDomain() } }

    override fun getTrendingTv(page: Int): Flow<Result<List<TvShow>>> =
        fetchFlow({ tvService.getTrending(page = page) }) { it.results.map { dto -> dto.toDomain() } }

    override fun getTopRatedTv(page: Int): Flow<Result<List<TvShow>>> =
        fetchFlow({ tvService.getTopRated(page) }) { it.results.map { dto -> dto.toDomain() } }

    override fun getTvDetail(id: Int): Flow<Result<TvShow>> =
        fetchFlow({ tvService.getTvDetail(id) }) { it.toDomain() }

    override fun getTvCredits(id: Int): Flow<Result<List<Cast>>> =
        fetchFlow({ tvService.getCredits(id) }) { response ->
            response.cast.map { it.toDomain() }
        }

    override fun getTvTrailers(id: Int): Flow<Result<List<Trailer>>> =
        fetchFlow({ tvService.getVideos(id) }) { response ->
            response.results.mapNotNull { it.toDomainOrNull() }
        }

    override fun getSimilarTv(id: Int, page: Int): Flow<Result<List<TvShow>>> =
        fetchFlow({ tvService.getSimilar(id, page) }) { it.results.map { dto -> dto.toDomain() } }

}
