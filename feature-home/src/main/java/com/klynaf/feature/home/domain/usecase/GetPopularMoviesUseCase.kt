package com.klynaf.feature.home.domain.usecase

import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.repository.MovieRepository
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(page: Int = 1): Flow<Result<List<Movie>>> = repository.getPopularMovies(page)
}
