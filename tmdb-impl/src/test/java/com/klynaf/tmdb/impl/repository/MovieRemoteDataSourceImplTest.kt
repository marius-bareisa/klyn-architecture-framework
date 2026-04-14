package com.klynaf.tmdb.impl.repository

import app.cash.turbine.test
import com.klynaf.core.domain.error.AuthException
import com.klynaf.core.domain.util.Result
import com.klynaf.testutils.UnconfinedTestDispatcherExtension
import com.klynaf.tmdb.api.dto.CastDto
import com.klynaf.tmdb.api.dto.CreditsResponse
import com.klynaf.tmdb.api.dto.MovieDto
import com.klynaf.tmdb.api.dto.TrailerDto
import com.klynaf.tmdb.api.dto.VideosResponse
import com.klynaf.tmdb.api.model.PagedResponse
import com.klynaf.tmdb.api.service.TmdbMovieService
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response

private val MOVIE_DTO =
    MovieDto(1, "Test Movie", "/poster.jpg", null, "Overview", 7.5, "2024-01-01")

private val CAST_DTO = CastDto(10, "Actor", "Hero", "/profile.jpg")
private val CREDITS = CreditsResponse(1, listOf(CAST_DTO))
private val TRAILER_DTO = TrailerDto("abc123", "Trailer", "YouTube", "Trailer")
private val VIDEOS = VideosResponse(1, listOf(TRAILER_DTO))

@ExtendWith(MockKExtension::class, UnconfinedTestDispatcherExtension::class)
internal class MovieRemoteDataSourceImplTest {

    private val movieService = mockk<TmdbMovieService>()
    private lateinit var movieRemoteDataSourceImpl: MovieRemoteDataSourceImpl

    @BeforeEach
    fun setUp() {
        movieRemoteDataSourceImpl = MovieRemoteDataSourceImpl(movieService)
    }

    @Nested
    inner class WhenNetworkIsAvailable {

        @Test
        fun `GIVEN valid response WHEN getPopularMovies THEN emits Loading then Success with data`() =
            runTest {
                coEvery { movieService.getPopular(any()) } returns
                        Response.success(PagedResponse(1, listOf(MOVIE_DTO), 1, 1))

                movieRemoteDataSourceImpl.getPopularMovies(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("Test Movie", (result as Result.Success).data.first().title)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getMovieDetail THEN emits Loading then Success with correct id`() =
            runTest {
                coEvery { movieService.getMovieDetail(1) } returns Response.success(MOVIE_DTO)

                movieRemoteDataSourceImpl.getMovieDetail(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals(1, (result as Result.Success).data.id)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getTrendingMovies THEN emits Loading then Success with data`() =
            runTest {
                coEvery { movieService.getTrending() } returns
                        Response.success(PagedResponse(1, listOf(MOVIE_DTO), 1, 1))

                movieRemoteDataSourceImpl.getTrendingMovies(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("Test Movie", (result as Result.Success).data.first().title)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getTopRatedMovies THEN emits Loading then Success with data`() =
            runTest {
                coEvery { movieService.getTopRated(any()) } returns
                        Response.success(PagedResponse(1, listOf(MOVIE_DTO), 1, 1))

                movieRemoteDataSourceImpl.getTopRatedMovies(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getCredits THEN emits Loading then Success with cast mapped`() =
            runTest {
                coEvery { movieService.getCredits(1) } returns Response.success(CREDITS)

                movieRemoteDataSourceImpl.getCredits(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("Actor", (result as Result.Success).data.first().name)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getTrailers THEN emits Loading then Success with trailers mapped`() =
            runTest {
                coEvery { movieService.getVideos(1) } returns Response.success(VIDEOS)

                movieRemoteDataSourceImpl.getTrailers(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("Trailer", (result as Result.Success).data.first().name)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getSimilarMovies THEN emits Loading then Success with data`() =
            runTest {
                coEvery { movieService.getSimilar(any(), any()) } returns
                        Response.success(PagedResponse(1, listOf(MOVIE_DTO), 1, 1))

                movieRemoteDataSourceImpl.getSimilarMovies(1, 1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    awaitComplete()
                }
            }
    }

    @Nested
    inner class WhenNetworkFails {

        @Test
        fun `GIVEN IOException WHEN getPopularMovies THEN emits Loading then Error`() = runTest {
            coEvery { movieService.getPopular(any()) } throws java.io.IOException("No network")

            movieRemoteDataSourceImpl.getPopularMovies(1).test {
                assertTrue(awaitItem() is Result.Loading)
                assertTrue(awaitItem() is Result.Error)
                awaitComplete()
            }
        }

        @Test
        fun `GIVEN 401 response WHEN getPopularMovies THEN emits AuthException error`() = runTest {
            coEvery { movieService.getPopular(any()) } returns
                    Response.error(401, "".toResponseBody(null))

            movieRemoteDataSourceImpl.getPopularMovies(1).test {
                assertTrue(awaitItem() is Result.Loading)
                val error = awaitItem() as Result.Error
                assertTrue(error.throwable is AuthException)
                awaitComplete()
            }
        }
    }
}
