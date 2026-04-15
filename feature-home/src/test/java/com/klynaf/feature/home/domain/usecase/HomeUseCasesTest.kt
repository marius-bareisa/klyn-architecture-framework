@file:Suppress("UnusedFlow")

package com.klynaf.feature.home.domain.usecase

import app.cash.turbine.test
import com.klynaf.core.domain.repository.MovieRepository
import com.klynaf.core.domain.repository.TvRepository
import com.klynaf.core.domain.util.Result
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class HomeUseCasesTest {

    private val movieRepository: MovieRepository = mockk()
    private val tvRepository: TvRepository = mockk()

    @BeforeEach
    fun setUp() {
        every { movieRepository.getPopularMovies(any()) } returns flowOf(Result.Success(emptyList()))
        every { movieRepository.getTrendingMovies(any()) } returns flowOf(Result.Success(emptyList()))
        every { movieRepository.getTopRatedMovies(any()) } returns flowOf(Result.Success(emptyList()))
        every { tvRepository.getPopularTv(any()) } returns flowOf(Result.Success(emptyList()))
        every { tvRepository.getTopRatedTv(any()) } returns flowOf(Result.Success(emptyList()))
    }

    @Nested
    inner class GetPopularMoviesUseCaseTest {
        private val useCase = GetPopularMoviesUseCase(movieRepository)

        @Test
        fun `WHEN invoke with default page THEN delegates to movieRepository getPopularMovies with page 1`() = runTest {
            useCase().test {
                awaitItem()
                awaitComplete()
            }
            verify { movieRepository.getPopularMovies(1) }
        }

        @Test
        fun `WHEN invoke with custom page THEN passes page to repository`() = runTest {
            useCase(page = 2).test {
                awaitItem()
                awaitComplete()
            }
            verify { movieRepository.getPopularMovies(2) }
        }
    }

    @Nested
    inner class GetTrendingMoviesUseCaseTest {
        private val useCase = GetTrendingMoviesUseCase(movieRepository)

        @Test
        fun `WHEN invoke with default page THEN delegates to movieRepository getTrendingMovies with page 1`() = runTest {
            useCase().test {
                awaitItem()
                awaitComplete()
            }
            verify { movieRepository.getTrendingMovies(1) }
        }

        @Test
        fun `WHEN invoke with custom page THEN passes page to repository`() = runTest {
            useCase(page = 2).test {
                awaitItem()
                awaitComplete()
            }
            verify { movieRepository.getTrendingMovies(2) }
        }
    }

    @Nested
    inner class GetTopRatedMoviesUseCaseTest {
        private val useCase = GetTopRatedMoviesUseCase(movieRepository)

        @Test
        fun `WHEN invoke with default page THEN delegates to movieRepository getTopRatedMovies with page 1`() = runTest {
            useCase().test {
                awaitItem()
                awaitComplete()
            }
            verify { movieRepository.getTopRatedMovies(1) }
        }

        @Test
        fun `WHEN invoke with custom page THEN passes page to repository`() = runTest {
            useCase(page = 2).test {
                awaitItem()
                awaitComplete()
            }
            verify { movieRepository.getTopRatedMovies(2) }
        }
    }

    @Nested
    inner class GetPopularTvUseCaseTest {
        private val useCase = GetPopularTvUseCase(tvRepository)

        @Test
        fun `WHEN invoke with default page THEN delegates to tvRepository getPopularTv with page 1`() = runTest {
            useCase().test {
                awaitItem()
                awaitComplete()
            }
            verify { tvRepository.getPopularTv(1) }
        }

        @Test
        fun `WHEN invoke with custom page THEN passes page to repository`() = runTest {
            useCase(page = 2).test {
                awaitItem()
                awaitComplete()
            }
            verify { tvRepository.getPopularTv(2) }
        }
    }

    @Nested
    inner class GetTopRatedTvUseCaseTest {
        private val useCase = GetTopRatedTvUseCase(tvRepository)

        @Test
        fun `WHEN invoke with default page THEN delegates to tvRepository getTopRatedTv with page 1`() = runTest {
            useCase().test {
                awaitItem()
                awaitComplete()
            }
            verify { tvRepository.getTopRatedTv(1) }
        }

        @Test
        fun `WHEN invoke with custom page THEN passes page to repository`() = runTest {
            useCase(page = 2).test {
                awaitItem()
                awaitComplete()
            }
            verify { tvRepository.getTopRatedTv(2) }
        }
    }
}
