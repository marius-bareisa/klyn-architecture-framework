package com.klynaf.data.repository

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.repository.MovieRepository
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.source.remote.MovieRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: MovieRemoteDataSource,
) : MovieRepository {

    override fun getPopularMovies(page: Int): Flow<Result<List<Movie>>> =
        remoteDataSource.getPopularMovies(page)

    override fun getTrendingMovies(page: Int): Flow<Result<List<Movie>>> =
        remoteDataSource.getTrendingMovies(page)

    override fun getTopRatedMovies(page: Int): Flow<Result<List<Movie>>> =
        remoteDataSource.getTopRatedMovies(page)

    override fun getMovieDetail(id: Int): Flow<Result<Movie>> =
        remoteDataSource.getMovieDetail(id)

    override fun getCredits(id: Int): Flow<Result<List<Cast>>> =
        remoteDataSource.getCredits(id)

    override fun getTrailers(id: Int): Flow<Result<List<Trailer>>> =
        remoteDataSource.getTrailers(id)

    override fun getSimilarMovies(id: Int, page: Int): Flow<Result<List<Movie>>> =
        remoteDataSource.getSimilarMovies(id, page)
}
