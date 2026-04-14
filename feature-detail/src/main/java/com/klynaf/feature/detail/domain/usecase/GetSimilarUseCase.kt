package com.klynaf.feature.detail.domain.usecase

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.repository.MovieRepository
import com.klynaf.core.domain.repository.TvRepository
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.util.mapResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSimilarUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
) {
    operator fun invoke(
        id: Int,
        mediaType: MediaType,
        page: Int = 1
    ): Flow<Result<List<MediaItem>>> =
        when (mediaType) {
            MediaType.Movie -> movieRepository.getSimilarMovies(id, page).map { result ->
                result.mapResult { list -> list.map { MediaItem.MovieItem(it) } }
            }

            MediaType.TvShow -> tvRepository.getSimilarTv(id, page).map { result ->
                result.mapResult { list -> list.map { MediaItem.TvItem(it) } }
            }
        }
}
