package com.klynaf.feature.watchlist.presentation

import app.cash.turbine.test
import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.feature.watchlist.domain.usecase.GetWatchlistUseCase
import com.klynaf.feature.watchlist.domain.usecase.RemoveFromWatchlistUseCase
import com.klynaf.testutils.UnconfinedTestDispatcherExtension
import com.klynaf.uicore.model.MediaTypeUi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
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
internal class WatchlistViewModelTest {

    private val getWatchlist: GetWatchlistUseCase = mockk()
    private val removeFromWatchlist: RemoveFromWatchlistUseCase = mockk()
    private val mapper = WatchlistUiModelMapper()

    private lateinit var viewModel: WatchlistViewModel

    private val movieItem = WatchlistItem(
        mediaId = 1,
        mediaType = MediaType.Movie,
        title = "Inception",
        posterPath = "/inception.jpg",
        addedAt = 1000L
    )

    private val tvItem = WatchlistItem(
        mediaId = 2,
        mediaType = MediaType.TvShow,
        title = "Breaking Bad",
        posterPath = "/bb.jpg",
        addedAt = 2000L
    )

    @BeforeEach
    fun setUp() {
        every { getWatchlist.invoke() } answers { flowOf(emptyList()) }
        coEvery { removeFromWatchlist.invoke(any(), any()) } returns Unit
    }

    private fun createViewModel() {
        viewModel = WatchlistViewModel(getWatchlist, removeFromWatchlist, mapper)
    }

    @Nested
    inner class Initialisation {

        @Test
        fun `GIVEN getWatchlist emits empty list WHEN viewModel created THEN state has no items and isEmpty is true`() =
            runTest {
                every { getWatchlist.invoke() } answers { flowOf(emptyList()) }

                createViewModel()

                val state = viewModel.state.value
                assertTrue(state.items.isEmpty())
                assertTrue(state.isEmpty)
            }

        @Test
        fun `GIVEN getWatchlist emits two items WHEN viewModel created THEN state contains two mapped items`() =
            runTest {
                every { getWatchlist.invoke() } answers { flowOf(listOf(movieItem, tvItem)) }

                createViewModel()

                val state = viewModel.state.value
                assertEquals(2, state.items.size)
            }
    }

    @Nested
    inner class Mapping {

        @Test
        fun `GIVEN WatchlistItem with MediaType Movie WHEN mapped THEN ui model has correct fields`() =
            runTest {
                every { getWatchlist.invoke() } answers { flowOf(listOf(movieItem)) }

                createViewModel()

                val item = viewModel.state.value.items[0]
                assertEquals(1, item.mediaId)
                assertEquals("movie", item.mediaTypeRoute)
                assertEquals(MediaTypeUi.Movie, item.mediaTypeUi)
                assertEquals("Inception", item.title)
                assertEquals("https://image.tmdb.org/t/p/w342/inception.jpg", item.posterUrl)
            }

        @Test
        fun `GIVEN WatchlistItem with MediaType TvShow WHEN mapped THEN ui model has correct fields`() =
            runTest {
                every { getWatchlist.invoke() } answers { flowOf(listOf(tvItem)) }

                createViewModel()

                val item = viewModel.state.value.items[0]
                assertEquals(2, item.mediaId)
                assertEquals("tv", item.mediaTypeRoute)
                assertEquals(MediaTypeUi.TvShow, item.mediaTypeUi)
                assertEquals("Breaking Bad", item.title)
                assertEquals("https://image.tmdb.org/t/p/w342/bb.jpg", item.posterUrl)
            }
    }

    @Nested
    inner class EmptyState {

        @Test
        fun `GIVEN getWatchlist emits non-empty list WHEN items present THEN isEmpty is false`() =
            runTest {
                every { getWatchlist.invoke() } answers { flowOf(listOf(movieItem)) }

                createViewModel()

                assertFalse(viewModel.state.value.isEmpty)
            }
    }

    @Nested
    inner class Navigation {

        @Test
        fun `WHEN onItemClicked THEN NavigateToDetail event is emitted with mediaId and mediaTypeRoute`() =
            runTest {
                every { getWatchlist.invoke() } answers { flowOf(listOf(movieItem)) }
                createViewModel()

                viewModel.navEvents.test {
                    viewModel.onItemClicked(viewModel.state.value.items[0])
                    val event = awaitItem()
                    assertEquals(WatchlistNavEvent.NavigateToDetail(1, "movie"), event)
                }
            }
    }

    @Nested
    inner class Removal {

        @Test
        fun `WHEN onItemRemoved THEN removeFromWatchlist is called with correct mediaId and mediaType`() =
            runTest {
                every { getWatchlist.invoke() } answers { flowOf(listOf(movieItem)) }
                createViewModel()

                viewModel.onItemRemoved(viewModel.state.value.items[0])

                coVerify(exactly = 1) { removeFromWatchlist.invoke(1, MediaType.Movie) }
            }
    }
}
