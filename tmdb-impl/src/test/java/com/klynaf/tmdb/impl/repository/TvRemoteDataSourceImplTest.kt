package com.klynaf.tmdb.impl.repository

import app.cash.turbine.test
import com.klynaf.core.domain.util.Result
import com.klynaf.testutils.UnconfinedTestDispatcherExtension
import com.klynaf.tmdb.api.dto.CastDto
import com.klynaf.tmdb.api.dto.CreditsResponse
import com.klynaf.tmdb.api.dto.TrailerDto
import com.klynaf.tmdb.api.dto.TvShowDto
import com.klynaf.tmdb.api.dto.VideosResponse
import com.klynaf.tmdb.api.model.PagedResponse
import com.klynaf.tmdb.api.service.TmdbTvService
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response

private val TV_DTO = TvShowDto(1, "Test Show", "/poster.jpg", null, "Overview", 8.0, "2024-01-01")

private val CAST_DTO = CastDto(10, "Actor", "Hero", "/profile.jpg")
private val CREDITS = CreditsResponse(1, listOf(CAST_DTO))
private val TRAILER_DTO = TrailerDto("abc123", "Trailer", "YouTube", "Trailer")
private val VIDEOS = VideosResponse(1, listOf(TRAILER_DTO))

@ExtendWith(MockKExtension::class, UnconfinedTestDispatcherExtension::class)
internal class TvRemoteDataSourceImplTest {

    private val tvService = mockk<TmdbTvService>()
    private lateinit var tvRemoteDataSourceImpl: TvRemoteDataSourceImpl

    @BeforeEach
    fun setUp() {
        tvRemoteDataSourceImpl = TvRemoteDataSourceImpl(tvService)
    }

    @Nested
    inner class WhenNetworkIsAvailable {

        @Test
        fun `GIVEN valid response WHEN getPopularTv THEN emits Loading then Success`() = runTest {
            coEvery { tvService.getPopular(any()) } returns
                    Response.success(PagedResponse(1, listOf(TV_DTO), 1, 1))

            tvRemoteDataSourceImpl.getPopularTv(1).test {
                assertTrue(awaitItem() is Result.Loading)
                val result = awaitItem()
                assertTrue(result is Result.Success)
                assertEquals("Test Show", (result as Result.Success).data.first().name)
                awaitComplete()
            }
        }

        @Test
        fun `GIVEN valid response WHEN getTrendingTv THEN emits Loading then Success with data`() =
            runTest {
                coEvery { tvService.getTrending() } returns
                        Response.success(PagedResponse(1, listOf(TV_DTO), 1, 1))

                tvRemoteDataSourceImpl.getTrendingTv(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("Test Show", (result as Result.Success).data.first().name)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getTopRatedTv THEN emits Loading then Success with data`() =
            runTest {
                coEvery { tvService.getTopRated(any()) } returns
                        Response.success(PagedResponse(1, listOf(TV_DTO), 1, 1))

                tvRemoteDataSourceImpl.getTopRatedTv(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getTvDetail THEN emits Loading then Success with correct id`() =
            runTest {
                coEvery { tvService.getTvDetail(1) } returns Response.success(TV_DTO)

                tvRemoteDataSourceImpl.getTvDetail(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals(1, (result as Result.Success).data.id)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getTvCredits THEN emits Loading then Success with cast mapped`() =
            runTest {
                coEvery { tvService.getCredits(1) } returns Response.success(CREDITS)

                tvRemoteDataSourceImpl.getTvCredits(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("Actor", (result as Result.Success).data.first().name)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getTvTrailers THEN emits Loading then Success with trailers mapped`() =
            runTest {
                coEvery { tvService.getVideos(1) } returns Response.success(VIDEOS)

                tvRemoteDataSourceImpl.getTvTrailers(1).test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("Trailer", (result as Result.Success).data.first().name)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN valid response WHEN getSimilarTv THEN emits Loading then Success with data`() =
            runTest {
                coEvery { tvService.getSimilar(any(), any()) } returns
                        Response.success(PagedResponse(1, listOf(TV_DTO), 1, 1))

                tvRemoteDataSourceImpl.getSimilarTv(1, 1).test {
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
        fun `GIVEN IOException WHEN getPopularTv THEN emits Loading then Error`() = runTest {
            coEvery { tvService.getPopular(any()) } throws java.io.IOException("No network")

            tvRemoteDataSourceImpl.getPopularTv(1).test {
                assertTrue(awaitItem() is Result.Loading)
                assertTrue(awaitItem() is Result.Error)
                awaitComplete()
            }
        }
    }
}
