package com.klynaf.data.repository

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.repository.TvRepository
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.source.remote.TvRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TvRepositoryImpl @Inject constructor(
    private val remoteDataSource: TvRemoteDataSource,
) : TvRepository {

    override fun getPopularTv(page: Int): Flow<Result<List<TvShow>>> =
        remoteDataSource.getPopularTv(page)

    override fun getTrendingTv(page: Int): Flow<Result<List<TvShow>>> =
        remoteDataSource.getTrendingTv(page)

    override fun getTopRatedTv(page: Int): Flow<Result<List<TvShow>>> =
        remoteDataSource.getTopRatedTv(page)

    override fun getTvDetail(id: Int): Flow<Result<TvShow>> =
        remoteDataSource.getTvDetail(id)

    override fun getTvCredits(id: Int): Flow<Result<List<Cast>>> =
        remoteDataSource.getTvCredits(id)

    override fun getTvTrailers(id: Int): Flow<Result<List<Trailer>>> =
        remoteDataSource.getTvTrailers(id)

    override fun getSimilarTv(id: Int, page: Int): Flow<Result<List<TvShow>>> =
        remoteDataSource.getSimilarTv(id, page)
}
