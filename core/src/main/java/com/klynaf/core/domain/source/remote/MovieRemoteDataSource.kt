package com.klynaf.core.domain.source.remote

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MovieRemoteDataSource {
    fun getPopularMovies(page: Int): Flow<Result<List<Movie>>>
    fun getTrendingMovies(page: Int): Flow<Result<List<Movie>>>
    fun getTopRatedMovies(page: Int): Flow<Result<List<Movie>>>
    fun getMovieDetail(id: Int): Flow<Result<Movie>>
    fun getCredits(id: Int): Flow<Result<List<Cast>>>
    fun getTrailers(id: Int): Flow<Result<List<Trailer>>>
    fun getSimilarMovies(id: Int, page: Int): Flow<Result<List<Movie>>>
}
