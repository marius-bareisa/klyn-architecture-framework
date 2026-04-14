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

class GetMediaDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
) {
    operator fun invoke(id: Int, mediaType: MediaType): Flow<Result<MediaItem>> =
        when (mediaType) {
            MediaType.Movie -> movieRepository.getMovieDetail(id).map { result ->
                result.mapResult { MediaItem.MovieItem(it) }
            }
            MediaType.TvShow -> tvRepository.getTvDetail(id).map { result ->
                result.mapResult { MediaItem.TvItem(it) }
            }
        }
}
