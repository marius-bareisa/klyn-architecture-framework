package com.klynaf.core.domain.repository

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovies(page: Int = 1): Flow<Result<List<Movie>>>
    fun getTrendingMovies(page: Int = 1): Flow<Result<List<Movie>>>
    fun getTopRatedMovies(page: Int = 1): Flow<Result<List<Movie>>>
    fun getMovieDetail(id: Int): Flow<Result<Movie>>
    fun getCredits(id: Int): Flow<Result<List<Cast>>>
    fun getTrailers(id: Int): Flow<Result<List<Trailer>>>
    fun getSimilarMovies(id: Int, page: Int = 1): Flow<Result<List<Movie>>>
}
