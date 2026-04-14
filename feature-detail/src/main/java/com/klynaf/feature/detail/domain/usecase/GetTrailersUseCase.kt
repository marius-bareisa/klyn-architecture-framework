package com.klynaf.feature.detail.domain.usecase

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.repository.MovieRepository
import com.klynaf.core.domain.repository.TvRepository
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrailersUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
) {
    operator fun invoke(id: Int, mediaType: MediaType): Flow<Result<List<Trailer>>> =
        when (mediaType) {
            MediaType.Movie -> movieRepository.getTrailers(id)
            MediaType.TvShow -> tvRepository.getTvTrailers(id)
        }
}
