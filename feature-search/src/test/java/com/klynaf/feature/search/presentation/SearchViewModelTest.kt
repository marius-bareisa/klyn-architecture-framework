@file:Suppress("UnusedFlow")

package com.klynaf.feature.search.presentation

import app.cash.turbine.test
import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.util.Result
import com.klynaf.feature.search.domain.usecase.SearchUseCase
import com.klynaf.testutils.UnconfinedTestDispatcherExtension
import com.klynaf.uicore.model.MediaTypeUi
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class, UnconfinedTestDispatcherExtension::class)
internal class SearchViewModelTest {

    private val searchUseCase: SearchUseCase = mockk()
    private val mapper = SearchResultUiModelMapper()

    private val movie = Movie(
        id = 1,
        title = "Batman",
        posterPath = "/batman.jpg",
        backdropPath = null,
        overview = "A hero",
        voteAverage = 8.5,
        releaseDate = "2022-06-15",
    )
    private val movieItem = MediaItem.MovieItem(movie)

    private val tvShow = TvShow(
        id = 2,
        name = "Gotham",
        posterPath = "/gotham.jpg",
        backdropPath = null,
        overview = "A city",
        voteAverage = 7.9,
        firstAirDate = "2014-09-22",
    )
    private val tvItem = MediaItem.TvItem(tvShow)

    private lateinit var viewModel: SearchViewModel

    @BeforeEach
    fun setUp() {
        every { searchUseCase(any(), any()) } answers { flowOf(Result.Success(emptyList())) }
    }

    private fun createViewModel() {
        viewModel = SearchViewModel(searchUseCase, mapper)
    }

    @Nested
    inner class QueryBehaviour {

        @Test
        fun `GIVEN query length is 1 WHEN onQueryChanged THEN state is reset and search is not called`() =
            runTest {
                createViewModel()
                viewModel.onQueryChanged("a")

                val state = viewModel.state.value
                assertEquals("a", state.query)
                assertTrue(state.items.isEmpty())
                assertFalse(state.isLoading)
                assertNull(state.errorThrowable)
                verify(exactly = 0) { searchUseCase(any(), any()) }
            }

        @Test
        fun `GIVEN query length is 0 WHEN onQueryCleared THEN state resets to default`() = runTest {
            createViewModel()
            viewModel.onQueryChanged("something")
            advanceTimeBy(301L)

            viewModel.onQueryCleared()

            assertEquals(SearchState(), viewModel.state.value)
        }

        @Test
        fun `GIVEN valid query WHEN search returns Loading THEN isLoading is true`() = runTest {
            every { searchUseCase(any(), any()) } answers { flowOf(Result.Loading) }
            createViewModel()

            viewModel.onQueryChanged("batman")
            advanceTimeBy(301L)

            assertTrue(viewModel.state.value.isLoading)
        }

        @Test
        fun `GIVEN valid query WHEN search returns Error THEN state has error and isLoading false`() =
            runTest {
                every { searchUseCase(any(), any()) } answers {
                    flowOf(Result.Error(Exception("network error")))
                }
                createViewModel()

                viewModel.onQueryChanged("batman")
                advanceTimeBy(301L)

                val state = viewModel.state.value
                assertFalse(state.isLoading)
                assertEquals("network error", state.errorThrowable?.message)
                assertTrue(state.items.isEmpty())
            }
    }

    @Nested
    inner class DataMappingAndTransformations {

        @Test
        fun `GIVEN valid query WHEN search returns mixed items THEN items are mapped correctly to UI models`() =
            runTest {
                every { searchUseCase(any(), any()) } answers {
                    flowOf(
                        Result.Loading,
                        Result.Success(listOf(movieItem, tvItem))
                    )
                }
                createViewModel()

                viewModel.onQueryChanged("DC Comics")
                advanceTimeBy(301L)

                val state = viewModel.state.value
                assertFalse(state.isLoading)
                assertEquals(2, state.items.size)
                assertNull(state.errorThrowable)

                val firstItem = state.items[0]
                assertEquals("Batman", firstItem.title)
                assertEquals("movie", firstItem.mediaTypeRoute)
                assertEquals(MediaTypeUi.Movie, firstItem.mediaTypeUi)
                assertEquals(1, firstItem.mediaId)

                val secondItem = state.items[1]
                assertEquals("Gotham", secondItem.title)
                assertEquals("tv", secondItem.mediaTypeRoute)
                assertEquals(MediaTypeUi.TvShow, secondItem.mediaTypeUi)
                assertEquals(2, secondItem.mediaId)
            }
    }

    @Nested
    inner class FlowOperators {

        @Test
        fun `GIVEN rapid query changes WHEN debounce applies THEN search is called only for the last query`() =
            runTest {
                createViewModel()

                viewModel.onQueryChanged("ba")
                viewModel.onQueryChanged("bat")
                viewModel.onQueryChanged("batm")
                viewModel.onQueryChanged("batman")
                advanceTimeBy(301L)

                verify(exactly = 1) { searchUseCase("batman", 1) }
                verify(exactly = 0) { searchUseCase("ba", any()) }
                verify(exactly = 0) { searchUseCase("bat", any()) }
                verify(exactly = 0) { searchUseCase("batm", any()) }
            }

        @Test
        fun `GIVEN same query emitted consecutively WHEN distinctUntilChanged applies THEN search is only called once`() =
            runTest {
                createViewModel()

                viewModel.onQueryChanged("batman")
                advanceTimeBy(301L)

                viewModel.onQueryChanged("batman")
                advanceTimeBy(301L)

                verify(exactly = 1) { searchUseCase("batman", 1) }
            }
    }

    @Nested
    inner class HasNoResults {

        @Test
        fun `GIVEN query length greater than 1 and no items WHEN state observed THEN hasNoResults is true`() =
            runTest {
                every {
                    searchUseCase(
                        any(),
                        any()
                    )
                } answers { flowOf(Result.Success(emptyList())) }
                createViewModel()

                viewModel.onQueryChanged("batman")
                advanceTimeBy(301L)

                assertTrue(viewModel.state.value.hasNoResults)
            }

        @Test
        fun `GIVEN query length is 1 WHEN state observed THEN hasNoResults is false`() = runTest {
            createViewModel()

            viewModel.onQueryChanged("b")

            assertFalse(viewModel.state.value.hasNoResults)
        }

        @Test
        fun `GIVEN query length greater than 1 and isLoading true WHEN state observed THEN hasNoResults is false`() =
            runTest {
                every { searchUseCase(any(), any()) } answers { flowOf(Result.Loading) }
                createViewModel()

                viewModel.onQueryChanged("batman")
                advanceTimeBy(301L)

                assertFalse(viewModel.state.value.hasNoResults)
            }
    }

    @Nested
    inner class RetryBehaviour {

        @Test
        fun `WHEN onRetry THEN searchUseCase is called again with the latest query`() = runTest {
            every { searchUseCase(any(), any()) } answers {
                flowOf(Result.Success(listOf(movieItem)))
            }
            createViewModel()

            viewModel.onQueryChanged("batman")
            advanceTimeBy(301L)

            viewModel.onRetry()
            advanceTimeBy(301L)

            verify(exactly = 2) { searchUseCase("batman", 1) }
        }
    }

    @Nested
    inner class Navigation {

        @Test
        fun `WHEN onItemClicked THEN NavigateToDetail event is emitted with correct args`() =
            runTest {
                createViewModel()

                viewModel.navEvents.test {
                    viewModel.onItemClicked(42, "movie")

                    val event = awaitItem()
                    assertEquals(SearchNavEvent.NavigateToDetail(42, "movie"), event)
                }
            }
    }
}
