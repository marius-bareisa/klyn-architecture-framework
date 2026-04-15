@file:Suppress("UnusedFlow")

package com.klynaf.feature.watchlist.domain.usecase

import app.cash.turbine.test
import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class WatchlistUseCasesTest {

    private val watchlistRepository: WatchlistRepository = mockk()
    private val watchlistItem = WatchlistItem(1, MediaType.Movie, "Inception", "/poster.jpg", 123456789L)

    @Nested
    inner class GetWatchlistUseCaseTest {
        private val useCase = GetWatchlistUseCase(watchlistRepository)

        @Test
        fun `WHEN invoke THEN delegates to watchlistRepository getAll`() = runTest {
            every { watchlistRepository.getAll() } returns flowOf(emptyList())
            
            useCase().test {
                awaitItem()
                awaitComplete()
            }
            
            verify { watchlistRepository.getAll() }
        }
    }

    @Nested
    inner class RemoveFromWatchlistUseCaseTest {
        private val useCase = RemoveFromWatchlistUseCase(watchlistRepository)

        @Test
        fun `WHEN invoke THEN delegates to watchlistRepository remove with correct arguments`() = runTest {
            coEvery { watchlistRepository.remove(1, MediaType.Movie) } returns Unit
            
            useCase(1, MediaType.Movie)
            
            coVerify { watchlistRepository.remove(1, MediaType.Movie) }
        }
    }

    @Nested
    inner class ToggleWatchlistUseCaseTest {
        private val useCase = ToggleWatchlistUseCase(watchlistRepository)

        @Test
        fun `WHEN invoke THEN delegates to watchlistRepository toggle with correct item`() = runTest {
            coEvery { watchlistRepository.toggle(watchlistItem) } returns Unit
            
            useCase(watchlistItem)
            
            coVerify { watchlistRepository.toggle(watchlistItem) }
        }
    }
}
