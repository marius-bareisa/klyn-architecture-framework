package com.klynaf.core.domain.source.remote

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface TvRemoteDataSource {
    fun getPopularTv(page: Int): Flow<Result<List<TvShow>>>
    fun getTrendingTv(page: Int): Flow<Result<List<TvShow>>>
    fun getTopRatedTv(page: Int): Flow<Result<List<TvShow>>>
    fun getTvDetail(id: Int): Flow<Result<TvShow>>
    fun getTvCredits(id: Int): Flow<Result<List<Cast>>>
    fun getTvTrailers(id: Int): Flow<Result<List<Trailer>>>
    fun getSimilarTv(id: Int, page: Int): Flow<Result<List<TvShow>>>
}
