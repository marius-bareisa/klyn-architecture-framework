@file:Suppress("UnusedFlow")

package com.klynaf.feature.detail.domain.usecase

import app.cash.turbine.test
import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.core.domain.repository.MovieRepository
import com.klynaf.core.domain.repository.TvRepository
import com.klynaf.core.domain.repository.WatchlistRepository
import com.klynaf.core.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class DetailUseCasesTest {

    private val mediaId = 1
    private val movie = Movie(1, "Inception", "/poster.jpg", "/backdrop.jpg", "Overview", 8.8, "2010-07-16")
    private val tvShow = TvShow(1, "Breaking Bad", "/poster.jpg", "/backdrop.jpg", "Overview", 9.5, "2008-01-20")
    private val cast = listOf(Cast(1, "Leonardo DiCaprio", "Cobb", "/leo.jpg"))
    private val trailers = listOf(Trailer("Official Trailer", "https://youtube.com/v1", "/thumb1.jpg", "Trailer"))
    private val watchlistItem = WatchlistItem(1, MediaType.Movie, "Inception", "/poster.jpg", 123456789L)

    private val movieRepository: MovieRepository = mockk()
    private val tvRepository: TvRepository = mockk()
    private val watchlistRepository: WatchlistRepository = mockk()

    @Nested
    inner class GetMediaDetailUseCaseTest {
        private val useCase = GetMediaDetailUseCase(movieRepository, tvRepository)

        @Test
        fun `GIVEN Movie mediaType WHEN invoke THEN calls movieRepository getMovieDetail and wraps result in MovieItem`() = runTest {
            every { movieRepository.getMovieDetail(mediaId) } returns flowOf(Result.Success(movie))
            
            useCase(mediaId, MediaType.Movie).test {
                val item = awaitItem()
                assertTrue(item is Result.Success && item.data is MediaItem.MovieItem)
                awaitComplete()
            }
            
            verify { movieRepository.getMovieDetail(mediaId) }
            verify(exactly = 0) { tvRepository.getTvDetail(any()) }
        }

        @Test
        fun `GIVEN TvShow mediaType WHEN invoke THEN calls tvRepository getTvDetail and wraps result in TvItem`() = runTest {
            every { tvRepository.getTvDetail(mediaId) } returns flowOf(Result.Success(tvShow))
            
            useCase(mediaId, MediaType.TvShow).test {
                val item = awaitItem()
                assertTrue(item is Result.Success && item.data is MediaItem.TvItem)
                awaitComplete()
            }
            
            verify { tvRepository.getTvDetail(mediaId) }
            verify(exactly = 0) { movieRepository.getMovieDetail(any()) }
        }

        @Test
        fun `GIVEN Movie mediaType WHEN invoke THEN does not call tvRepository`() = runTest {
            every { movieRepository.getMovieDetail(mediaId) } returns flowOf(Result.Success(movie))
            
            useCase(mediaId, MediaType.Movie).test {
                awaitItem()
                awaitComplete()
            }
            
            verify(exactly = 0) { tvRepository.getTvDetail(any()) }
        }
    }

    @Nested
    inner class GetCreditsUseCaseTest {
        private val useCase = GetCreditsUseCase(movieRepository, tvRepository)

        @Test
        fun `GIVEN Movie mediaType WHEN invoke THEN calls movieRepository getCredits`() = runTest {
            every { movieRepository.getCredits(mediaId) } returns flowOf(Result.Success(cast))
            
            useCase(mediaId, MediaType.Movie).test {
                assertTrue(awaitItem() is Result.Success)
                awaitComplete()
            }
            
            verify { movieRepository.getCredits(mediaId) }
            verify(exactly = 0) { tvRepository.getTvCredits(any()) }
        }

        @Test
        fun `GIVEN TvShow mediaType WHEN invoke THEN calls tvRepository getTvCredits`() = runTest {
            every { tvRepository.getTvCredits(mediaId) } returns flowOf(Result.Success(cast))
            
            useCase(mediaId, MediaType.TvShow).test {
                assertTrue(awaitItem() is Result.Success)
                awaitComplete()
            }
            
            verify { tvRepository.getTvCredits(mediaId) }
            verify(exactly = 0) { movieRepository.getCredits(any()) }
        }
    }

    @Nested
    inner class GetTrailersUseCaseTest {
        private val useCase = GetTrailersUseCase(movieRepository, tvRepository)

        @Test
        fun `GIVEN Movie mediaType WHEN invoke THEN calls movieRepository getTrailers`() = runTest {
            every { movieRepository.getTrailers(mediaId) } returns flowOf(Result.Success(trailers))
            
            useCase(mediaId, MediaType.Movie).test {
                assertTrue(awaitItem() is Result.Success)
                awaitComplete()
            }
            
            verify { movieRepository.getTrailers(mediaId) }
            verify(exactly = 0) { tvRepository.getTvTrailers(any()) }
        }

        @Test
        fun `GIVEN TvShow mediaType WHEN invoke THEN calls tvRepository getTvTrailers`() = runTest {
            every { tvRepository.getTvTrailers(mediaId) } returns flowOf(Result.Success(trailers))
            
            useCase(mediaId, MediaType.TvShow).test {
                assertTrue(awaitItem() is Result.Success)
                awaitComplete()
            }
            
            verify { tvRepository.getTvTrailers(mediaId) }
            verify(exactly = 0) { movieRepository.getTrailers(any()) }
        }
    }

    @Nested
    inner class GetSimilarUseCaseTest {
        private val useCase = GetSimilarUseCase(movieRepository, tvRepository)

        @Test
        fun `GIVEN Movie mediaType WHEN invoke THEN calls movieRepository getSimilarMovies and wraps each item in MovieItem`() = runTest {
            every { movieRepository.getSimilarMovies(mediaId, 1) } returns flowOf(Result.Success(listOf(movie)))
            
            useCase(mediaId, MediaType.Movie).test {
                val result = awaitItem()
                assertTrue(result is Result.Success)
                assertTrue((result as Result.Success).data.first() is MediaItem.MovieItem)
                awaitComplete()
            }
            
            verify { movieRepository.getSimilarMovies(mediaId, 1) }
            verify(exactly = 0) { tvRepository.getSimilarTv(any(), any()) }
        }

        @Test
        fun `GIVEN TvShow mediaType WHEN invoke THEN calls tvRepository getSimilarTv and wraps each item in TvItem`() = runTest {
            every { tvRepository.getSimilarTv(mediaId, 1) } returns flowOf(Result.Success(listOf(tvShow)))
            
            useCase(mediaId, MediaType.TvShow).test {
                val result = awaitItem()
                assertTrue(result is Result.Success)
                assertTrue((result as Result.Success).data.first() is MediaItem.TvItem)
                awaitComplete()
            }
            
            verify { tvRepository.getSimilarTv(mediaId, 1) }
            verify(exactly = 0) { movieRepository.getSimilarMovies(any(), any()) }
        }

        @Test
        fun `GIVEN Movie mediaType WHEN invoke with custom page THEN passes page to repository`() = runTest {
            every { movieRepository.getSimilarMovies(mediaId, 3) } returns flowOf(Result.Success(emptyList()))
            
            useCase(mediaId, MediaType.Movie, page = 3).test {
                awaitItem()
                awaitComplete()
            }
            
            verify { movieRepository.getSimilarMovies(mediaId, 3) }
        }
    }

    @Nested
    inner class GetWatchlistStatusUseCaseTest {
        private val useCase = GetWatchlistStatusUseCase(watchlistRepository)

        @Test
        fun `WHEN invoke THEN delegates to watchlistRepository isWatchlisted with correct arguments`() = runTest {
            every { watchlistRepository.isWatchlisted(mediaId, MediaType.Movie) } returns flowOf(true)
            
            useCase(mediaId, MediaType.Movie).test {
                assertTrue(awaitItem())
                awaitComplete()
            }
            
            verify { watchlistRepository.isWatchlisted(mediaId, MediaType.Movie) }
        }
    }

    @Nested
    inner class ToggleWatchlistUseCaseTest {
        private val useCase = ToggleWatchlistUseCase(watchlistRepository)

        @Test
        fun `WHEN invoke THEN delegates to watchlistRepository toggle with the correct item`() = runTest {
            coEvery { watchlistRepository.toggle(watchlistItem) } returns Unit
            
            useCase(watchlistItem)
            
            coVerify { watchlistRepository.toggle(watchlistItem) }
        }
    }
}
