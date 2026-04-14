@file:Suppress("UnusedFlow")

package com.klynaf.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.uicore.model.MediaCardUiModel
import com.klynaf.core.domain.util.Result
import com.klynaf.core.util.TimeProvider
import com.klynaf.feature.detail.domain.usecase.DetailUseCases
import com.klynaf.testutils.UnconfinedTestDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class, UnconfinedTestDispatcherExtension::class)
internal class DetailViewModelTest {

    private val useCases: DetailUseCases = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val mapper: DetailUiModelMapper = mockk()

    private val mediaId = 1
    private val movieTitle = "Inception"
    private val tvShowName = "Breaking Bad"
    private val timestamp = 123456789L

    private val movie =
        Movie(mediaId, movieTitle, "/poster.jpg", "/backdrop.jpg", "Overview", 8.8, "2010-07-16")
    private val tvShow =
        TvShow(mediaId, tvShowName, "/poster.jpg", "/backdrop.jpg", "Overview", 9.5, "2008-01-20")
    private val cast = listOf(Cast(1, "Leonardo DiCaprio", "Cobb", "/leo.jpg"))
    private val trailers =
        listOf(Trailer("Official Trailer", "https://youtube.com/v1", "/thumb1.jpg", "Trailer"))
    private val similar = listOf(MediaItem.MovieItem(movie))

    private lateinit var viewModel: DetailViewModel

    private fun movieFlow() = flowOf(Result.Success(MediaItem.MovieItem(movie)))
    private fun tvShowFlow() = flowOf(Result.Success(MediaItem.TvItem(tvShow)))
    private fun castFlow() = flowOf(Result.Success(cast))
    private fun trailersFlow() = flowOf(Result.Success(trailers))
    private fun similarFlow() = flowOf(Result.Success(similar))
    private fun watchlistStatusFlow() = flowOf(false)

    @BeforeEach
    fun setUp() {
        every { timeProvider.now() } returns timestamp
        every { useCases.getWatchlistStatus(any(), any()) } answers { watchlistStatusFlow() }
        every { useCases.getMediaDetail(any(), any()) } answers {
            val type = it.invocation.args[1] as MediaType
            if (type == MediaType.Movie) movieFlow() else tvShowFlow()
        }
        every { useCases.getCredits(any(), any()) } answers { castFlow() }
        every { useCases.getTrailers(any(), any()) } answers { trailersFlow() }
        every { useCases.getSimilar(any(), any()) } answers { similarFlow() }

        every { mapper.mapMovie(any()) } returns DetailUiModel.MovieDetail(
            title = movieTitle,
            posterPath = "/poster.jpg",
            overview = "Overview",
            releaseYear = "2010",
            formattedRating = "8.8",
            backdropUrl = "backdrop_url",
        )
        every { mapper.mapTvShow(any()) } returns DetailUiModel.TvDetail(
            title = tvShowName,
            posterPath = "/poster.jpg",
            overview = "Overview",
            releaseYear = "2008",
            formattedRating = "9.5",
            backdropUrl = "backdrop_url",
        )
        every { mapper.mapCast(any()) } returns
            CastUiModel(id = 1, name = "Leonardo DiCaprio", character = "Cobb", profileUrl = "profile_url")
        every { mapper.mapTrailer(any()) } returns
            TrailerUiModel(name = "Official Trailer", thumbnailUrl = "/thumb1.jpg", videoUrl = "https://youtube.com/v1")
        every { mapper.mapMediaItem(any()) } returns
            MediaCardUiModel(
                uniqueId = "1",
                posterUrl = "poster_url",
                title = "Inception",
                voteAverage = 8.8,
                mediaId = 1,
                mediaTypeRoute = "movie"
            )
    }

    private fun createViewModel(mediaType: MediaType) {
        val savedStateHandle = SavedStateHandle(
            mapOf("mediaId" to mediaId, "mediaType" to mediaType.toRoute())
        )
        viewModel = DetailViewModel(
            savedStateHandle,
            useCases,
            timeProvider,
            mapper
        )
    }

    @Nested
    inner class InitialState {

        @Test
        fun `GIVEN movie WHEN init THEN state is loaded correctly`() = runTest {
            createViewModel(MediaType.Movie)

            val state = viewModel.state.value
            assertTrue(state.media is Result.Success)
            assertEquals(movieTitle, state.title)
            assertTrue(state.cast is Result.Success)
            assertTrue(state.trailers is Result.Success)
            assertTrue(state.similar is Result.Success)
            assertFalse(state.isWatchlisted)
            assertFalse(state.isRefreshing)
        }

        @Test
        fun `GIVEN tv show WHEN init THEN state is loaded correctly`() = runTest {
            createViewModel(MediaType.TvShow)

            val state = viewModel.state.value
            assertTrue(state.media is Result.Success)
            assertEquals(tvShowName, state.title)
        }

        @Test
        fun `GIVEN media not success WHEN title THEN returns empty string`() = runTest {
            every { useCases.getMediaDetail(any(), any()) } answers { flowOf(Result.Loading) }
            createViewModel(MediaType.Movie)

            assertEquals("", viewModel.state.value.title)
        }
    }

    @Nested
    inner class RefreshAndRetry {

        @Test
        fun `WHEN onRefresh THEN reloads all sections`() = runTest {
            createViewModel(MediaType.Movie)

            viewModel.onRefresh()

            verify(atLeast = 2) { useCases.getMediaDetail(mediaId, MediaType.Movie) }
            verify(atLeast = 2) { useCases.getCredits(mediaId, MediaType.Movie) }
            assertFalse(viewModel.state.value.isRefreshing)
        }

        @Test
        fun `GIVEN sections in error WHEN onRefresh THEN reloads all sections`() = runTest {
            every { useCases.getMediaDetail(any(), any()) } answers { flowOf(Result.Error(Exception())) }
            createViewModel(MediaType.Movie)

            viewModel.onRefresh()

            verify(exactly = 2) { useCases.getMediaDetail(mediaId, MediaType.Movie) }
        }

        @Test
        fun `WHEN onRetry MEDIA THEN reloads all sections`() = runTest {
            createViewModel(MediaType.Movie)

            viewModel.onRetry(DetailSection.MEDIA)

            verify(exactly = 2) { useCases.getMediaDetail(mediaId, MediaType.Movie) }
            verify(exactly = 2) { useCases.getCredits(mediaId, MediaType.Movie) }
            verify(exactly = 2) { useCases.getTrailers(mediaId, MediaType.Movie) }
            verify(exactly = 2) { useCases.getSimilar(mediaId, MediaType.Movie) }
        }

        @Test
        fun `WHEN onRetry CAST THEN reloads only cast`() = runTest {
            createViewModel(MediaType.Movie)

            viewModel.onRetry(DetailSection.CAST)

            verify(exactly = 2) { useCases.getCredits(mediaId, MediaType.Movie) }
            verify(exactly = 1) { useCases.getMediaDetail(mediaId, MediaType.Movie) }
        }

        @Test
        fun `WHEN onRetry TRAILERS THEN reloads only trailers`() = runTest {
            createViewModel(MediaType.Movie)

            viewModel.onRetry(DetailSection.TRAILERS)

            verify(exactly = 2) { useCases.getTrailers(mediaId, MediaType.Movie) }
        }

        @Test
        fun `WHEN onRetry SIMILAR THEN reloads only similar`() = runTest {
            createViewModel(MediaType.Movie)

            viewModel.onRetry(DetailSection.SIMILAR)

            verify(exactly = 2) { useCases.getSimilar(mediaId, MediaType.Movie) }
        }

        @Test
        fun `GIVEN tv show WHEN onRefresh THEN reloads tv detail`() = runTest {
            createViewModel(MediaType.TvShow)

            viewModel.onRefresh()

            verify(atLeast = 2) { useCases.getMediaDetail(mediaId, MediaType.TvShow) }
        }
    }

    @Nested
    inner class Watchlist {

        @Test
        fun `WHEN onWatchlistToggled THEN calls toggleWatchlist`() = runTest {
            createViewModel(MediaType.Movie)
            coEvery { useCases.toggleWatchlist(any()) } returns Unit

            viewModel.navEvents.test {
                viewModel.onWatchlistToggled()

                val expectedItem =
                    WatchlistItem(mediaId, MediaType.Movie, movieTitle, movie.posterPath, timestamp)
                coVerify { useCases.toggleWatchlist(expectedItem) }
                assertTrue(awaitItem() is DetailNavEvent.ShowWatchlistSnackbar)
            }
        }

        @Test
        fun `WHEN onWatchlistToggled rapid clicks THEN cancels previous job`() = runTest {
            createViewModel(MediaType.Movie)
            coEvery { useCases.toggleWatchlist(any()) } coAnswers { kotlinx.coroutines.delay(1000) }

            viewModel.onWatchlistToggled()
            viewModel.onWatchlistToggled()

            coVerify(atMost = 2) { useCases.toggleWatchlist(any()) }
        }

        @Test
        fun `GIVEN media load failed WHEN onWatchlistToggled THEN does nothing`() = runTest {
            every { useCases.getMediaDetail(any(), any()) } answers { flowOf(Result.Error(Exception())) }
            createViewModel(MediaType.Movie)

            viewModel.onWatchlistToggled()

            coVerify(exactly = 0) { useCases.toggleWatchlist(any()) }
        }
    }

    @Nested
    inner class StateExtensions {
        @Test
        fun `WHEN clearErrorsForRefresh THEN resets errors to Loading but keeps Success`() {
            val state = DetailState(
                media = Result.Error(Exception()),
                cast = Result.Success(emptyList()),
                trailers = Result.Error(Exception()),
                similar = Result.Loading
            )

            val updated = state.clearErrorsForRefresh()

            assertTrue(updated.isRefreshing)
            assertTrue(updated.media is Result.Loading)
            assertTrue(updated.cast is Result.Success)
            assertTrue(updated.trailers is Result.Loading)
            assertTrue(updated.similar is Result.Loading)
        }
    }

    @Nested
    inner class Navigation {

        @Test
        fun `WHEN onTrailerClicked THEN emits event`() = runTest {
            createViewModel(MediaType.Movie)
            val trailer = trailers.first()
            val trailerUiModel = TrailerUiModel(
                name = trailer.name,
                thumbnailUrl = trailer.thumbnailUrl,
                videoUrl = trailer.videoUrl
            )

            viewModel.navEvents.test {
                viewModel.onTrailerClicked(trailerUiModel)
                assertEquals(DetailNavEvent.TrailerClicked(trailerUiModel.videoUrl), awaitItem())
            }
        }

        @Test
        fun `WHEN onSimilarItemClicked THEN emits event`() = runTest {
            createViewModel(MediaType.Movie)

            viewModel.navEvents.test {
                viewModel.onSimilarItemClicked(2, "tv")
                assertEquals(
                    DetailNavEvent.NavigateToDetail(2, MediaType.TvShow.toRoute()),
                    awaitItem()
                )
            }
        }
    }

    @Nested
    inner class LoadingAndErrorStates {

        @Test
        fun `GIVEN media loading WHEN isAnySectionLoading THEN returns true`() = runTest {
            every { useCases.getMediaDetail(any(), any()) } answers { flowOf(Result.Loading) }
            createViewModel(MediaType.Movie)
            assertTrue(viewModel.state.value.isAnySectionLoading)
        }

        @Test
        fun `GIVEN all loaded WHEN isAnySectionLoading THEN returns false`() = runTest {
            createViewModel(MediaType.Movie)
            assertFalse(viewModel.state.value.isAnySectionLoading)
        }

        @Test
        fun `GIVEN cast loading WHEN isAnySectionLoading THEN returns true`() = runTest {
            every { useCases.getCredits(any(), any()) } answers { flowOf(Result.Loading) }
            createViewModel(MediaType.Movie)
            assertTrue(viewModel.state.value.isAnySectionLoading)
        }

        @Test
        fun `GIVEN trailers loading WHEN isAnySectionLoading THEN returns true`() = runTest {
            every { useCases.getTrailers(any(), any()) } answers { flowOf(Result.Loading) }
            createViewModel(MediaType.Movie)
            assertTrue(viewModel.state.value.isAnySectionLoading)
        }

        @Test
        fun `GIVEN similar loading WHEN isAnySectionLoading THEN returns true`() = runTest {
            every { useCases.getSimilar(any(), any()) } answers { flowOf(Result.Loading) }
            createViewModel(MediaType.Movie)
            assertTrue(viewModel.state.value.isAnySectionLoading)
        }
    }
}
