@file:Suppress("UnusedFlow")

package com.klynaf.feature.search.domain.usecase

import app.cash.turbine.test
import com.klynaf.core.domain.repository.SearchRepository
import com.klynaf.core.domain.util.Result
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class SearchUseCaseTest {

    private val searchRepository: SearchRepository = mockk()
    private val useCase = SearchUseCase(searchRepository)

    @Test
    fun `WHEN invoke THEN delegates to searchRepository search with correct query and page`() = runTest {
        every { searchRepository.search("batman", 1) } returns flowOf(Result.Success(emptyList()))
        
        useCase("batman", 1).test {
            awaitItem()
            awaitComplete()
        }
        
        verify { searchRepository.search("batman", 1) }
    }

    @Test
    fun `WHEN invoke with custom page THEN passes page to repository`() = runTest {
        every { searchRepository.search("batman", 2) } returns flowOf(Result.Success(emptyList()))
        
        useCase("batman", 2).test {
            awaitItem()
            awaitComplete()
        }
        
        verify { searchRepository.search("batman", 2) }
    }
}
