package com.klynaf.feature.home.domain.usecase

import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.repository.TvRepository
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopRatedTvUseCase @Inject constructor(private val repository: TvRepository) {
    operator fun invoke(page: Int = 1): Flow<Result<List<TvShow>>> = repository.getTopRatedTv(page)
}
