package com.klynaf.feature.search.domain.usecase

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.repository.SearchRepository
import com.klynaf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    operator fun invoke(query: String, page: Int = 1): Flow<Result<List<MediaItem>>> = searchRepository.search(query, page)
}
