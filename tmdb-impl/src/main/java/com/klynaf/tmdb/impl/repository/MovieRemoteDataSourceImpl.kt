package com.klynaf.tmdb.impl.repository

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.source.remote.MovieRemoteDataSource
import com.klynaf.core.domain.util.Result
import com.klynaf.tmdb.api.service.TmdbMovieService
import com.klynaf.tmdb.impl.mapper.toDomain
import com.klynaf.tmdb.impl.mapper.toDomainOrNull
import com.klynaf.tmdb.impl.util.fetchFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRemoteDataSourceImpl @Inject constructor(
    private val movieService: TmdbMovieService,
) : MovieRemoteDataSource {

    override fun getPopularMovies(page: Int): Flow<Result<List<Movie>>> =
        fetchFlow({ movieService.getPopular(page) }) { it.results.map { dto -> dto.toDomain() } }

    override fun getTrendingMovies(page: Int): Flow<Result<List<Movie>>> =
        fetchFlow({ movieService.getTrending(page = page) }) { it.results.map { dto -> dto.toDomain() } }

    override fun getTopRatedMovies(page: Int): Flow<Result<List<Movie>>> =
        fetchFlow({ movieService.getTopRated(page) }) { it.results.map { dto -> dto.toDomain() } }

    override fun getMovieDetail(id: Int): Flow<Result<Movie>> =
        fetchFlow({ movieService.getMovieDetail(id) }) { it.toDomain() }

    override fun getCredits(id: Int): Flow<Result<List<Cast>>> =
        fetchFlow({ movieService.getCredits(id) }) { response ->
            response.cast.map { it.toDomain() }
        }

    override fun getTrailers(id: Int): Flow<Result<List<Trailer>>> =
        fetchFlow({ movieService.getVideos(id) }) { response ->
            response.results.mapNotNull { it.toDomainOrNull() }
        }

    override fun getSimilarMovies(id: Int, page: Int): Flow<Result<List<Movie>>> =
        fetchFlow({
            movieService.getSimilar(
                id,
                page
            )
        }) { it.results.map { dto -> dto.toDomain() } }
}
