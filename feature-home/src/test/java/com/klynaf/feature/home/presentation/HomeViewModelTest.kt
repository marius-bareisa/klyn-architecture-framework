@file:Suppress("UnusedFlow")

package com.klynaf.feature.home.presentation

import app.cash.turbine.test
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.TvShow
import com.klynaf.core.domain.util.Result
import com.klynaf.feature.home.domain.usecase.HomeUseCases
import com.klynaf.testutils.UnconfinedTestDispatcherExtension
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class, UnconfinedTestDispatcherExtension::class)
internal class HomeViewModelTest {

    private val useCases: HomeUseCases = mockk()
    private val mapper = HomeUiModelMapper()

    private val threshold = HomeViewModel.LOAD_MORE_THRESHOLD

    private val movie = Movie(1, "Movie", "/p.jpg", "/b.jpg", "Overview", 8.0, "2024-01-01")
    private val movie2 = Movie(3, "Movie2", "/p3.jpg", "/b3.jpg", "Overview3", 7.0, "2024-03-03")
    private val tvShow = TvShow(2, "TV", "/p2.jpg", "/b2.jpg", "Overview2", 9.0, "2024-02-02")

    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Success(listOf(movie))) }
        every { useCases.getPopularMovies(any()) } answers { flowOf(Result.Success(listOf(movie))) }
        every { useCases.getPopularTv(any()) } answers { flowOf(Result.Success(listOf(tvShow))) }
        every { useCases.getTopRatedMovies(any()) } answers { flowOf(Result.Success(listOf(movie))) }
        every { useCases.getTopRatedTv(any()) } answers { flowOf(Result.Success(listOf(tvShow))) }
    }

    private fun createViewModel() {
        viewModel = HomeViewModel(useCases, mapper)
    }

    @Nested
    inner class InitialState {

        @Test
        fun `WHEN init THEN all sections are loaded`() = runTest {
            createViewModel()

            val state = viewModel.state.value
            assertInstanceOf(SectionState.Success::class.java, state.trending)
            assertInstanceOf(SectionState.Success::class.java, state.popularMovies)
            assertInstanceOf(SectionState.Success::class.java, state.popularTv)
            assertInstanceOf(SectionState.Success::class.java, state.topRatedMovies)
            assertInstanceOf(SectionState.Success::class.java, state.topRatedTv)
            assertFalse(state.isRefreshing)
        }

        @Test
        fun `WHEN init THEN each section contains mapped items`() = runTest {
            createViewModel()

            val trending = viewModel.state.value.trending as SectionState.Success
            assertEquals(1, trending.items.size)
            assertEquals("movie_1", trending.items.first().uniqueId)
        }

        @Test
        fun `GIVEN use case returns loading WHEN init THEN that section is loading`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Loading) }
            createViewModel()

            assertInstanceOf(SectionState.Loading::class.java, viewModel.state.value.trending)
        }

        @Test
        fun `GIVEN section is loading THEN isAnySectionLoading is true`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Loading) }
            createViewModel()

            assertTrue(viewModel.state.value.isAnySectionLoading)
        }

        @Test
        fun `GIVEN all sections loaded THEN isAnySectionLoading is false`() = runTest {
            createViewModel()

            assertFalse(viewModel.state.value.isAnySectionLoading)
        }
    }

    @Nested
    inner class ErrorStates {

        @Test
        fun `GIVEN one section errors WHEN init THEN that section is Error and others are Success`() =
            runTest {
                every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Error(Exception())) }
                createViewModel()

                val state = viewModel.state.value
                assertInstanceOf(SectionState.Error::class.java, state.trending)
                assertInstanceOf(SectionState.Success::class.java, state.popularMovies)
                assertInstanceOf(SectionState.Success::class.java, state.popularTv)
                assertInstanceOf(SectionState.Success::class.java, state.topRatedMovies)
                assertInstanceOf(SectionState.Success::class.java, state.topRatedTv)
            }

        @Test
        fun `GIVEN section is Error THEN error throwable is preserved`() = runTest {
            val error = RuntimeException("Something went wrong")
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Error(error)) }
            createViewModel()

            val section = viewModel.state.value.trending as SectionState.Error
            assertEquals(error, section.throwable)
        }
    }

    @Nested
    inner class Refresh {

        @Test
        fun `WHEN onRefresh THEN reloads all sections`() = runTest {
            createViewModel()

            viewModel.onRefresh()

            verify(exactly = 2) { useCases.getTrendingMovies(any()) }
            verify(exactly = 2) { useCases.getPopularMovies(any()) }
            verify(exactly = 2) { useCases.getPopularTv(any()) }
            verify(exactly = 2) { useCases.getTopRatedMovies(any()) }
            verify(exactly = 2) { useCases.getTopRatedTv(any()) }
        }

        @Test
        fun `WHEN onRefresh completes THEN isRefreshing is false`() = runTest {
            createViewModel()

            viewModel.onRefresh()

            assertFalse(viewModel.state.value.isRefreshing)
        }

        @Test
        fun `GIVEN sections in error WHEN onRefresh THEN reloads all sections`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Error(Exception())) }
            createViewModel()

            viewModel.onRefresh()

            verify(exactly = 2) { useCases.getTrendingMovies(any()) }
        }

        @Test
        fun `GIVEN any section is loading WHEN onRefresh THEN does nothing`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Loading) }
            createViewModel()

            viewModel.onRefresh()

            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
        }
    }

    @Nested
    inner class Retry {

        @Test
        fun `GIVEN section is Loading WHEN onRetry THEN does nothing`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Loading) }
            createViewModel()

            viewModel.onRetry(HomeCategory.TRENDING)

            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
        }

        @Test
        fun `GIVEN section is Error WHEN onRetry THEN reloads page 1`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Error(Exception())) }
            createViewModel()
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Success(listOf(movie))) }

            viewModel.onRetry(HomeCategory.TRENDING)

            val section = viewModel.state.value.trending as SectionState.Success
            assertEquals(1, section.page)
            assertEquals(1, section.items.size)
        }

        @Test
        fun `GIVEN section is Success WHEN onRetry THEN loads next page`() = runTest {
            every { useCases.getTrendingMovies(2) } answers { flowOf(Result.Success(listOf(movie2))) }
            createViewModel()

            viewModel.onRetry(HomeCategory.TRENDING)

            val section = viewModel.state.value.trending as SectionState.Success
            assertEquals(2, section.page)
            assertEquals(2, section.items.size)
        }

        @Test
        fun `WHEN onRetry TRENDING THEN reloads only trending`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.TRENDING)

            verify(exactly = 2) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getPopularTv(any()) }
            verify(exactly = 1) { useCases.getTopRatedMovies(any()) }
            verify(exactly = 1) { useCases.getTopRatedTv(any()) }
        }

        @Test
        fun `WHEN onRetry POPULAR_MOVIES THEN reloads only popular movies`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.POPULAR_MOVIES)

            verify(exactly = 2) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularTv(any()) }
            verify(exactly = 1) { useCases.getTopRatedMovies(any()) }
            verify(exactly = 1) { useCases.getTopRatedTv(any()) }
        }

        @Test
        fun `WHEN onRetry POPULAR_TV THEN reloads only popular TV`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.POPULAR_TV)

            verify(exactly = 2) { useCases.getPopularTv(any()) }
            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getTopRatedMovies(any()) }
            verify(exactly = 1) { useCases.getTopRatedTv(any()) }
        }

        @Test
        fun `WHEN onRetry TOP_RATED_MOVIES THEN reloads only top rated movies`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.TOP_RATED_MOVIES)

            verify(exactly = 2) { useCases.getTopRatedMovies(any()) }
            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getPopularTv(any()) }
            verify(exactly = 1) { useCases.getTopRatedTv(any()) }
        }

        @Test
        fun `WHEN onRetry TOP_RATED_TV THEN reloads only top rated TV`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.TOP_RATED_TV)

            verify(exactly = 2) { useCases.getTopRatedTv(any()) }
            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getPopularTv(any()) }
            verify(exactly = 1) { useCases.getTopRatedMovies(any()) }
        }
    }

    @Nested
    inner class LoadMore {

        @Test
        fun `GIVEN scroll at threshold WHEN onScrollPositionChanged THEN loads next page`() =
            runTest {
                every { useCases.getTrendingMovies(2) } answers {
                    flowOf(
                        Result.Success(
                            listOf(
                                movie2
                            )
                        )
                    )
                }
                createViewModel()

                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold,
                    totalItems = threshold * 2
                )

                val section = viewModel.state.value.trending as SectionState.Success
                assertEquals(2, section.page)
                assertEquals(2, section.items.size)
                assertInstanceOf(LoadMoreState.Idle::class.java, section.loadMoreState)
            }

        @Test
        fun `GIVEN page 2 returns empty list THEN hasReachedEnd is true`() = runTest {
            every { useCases.getTrendingMovies(2) } answers { flowOf(Result.Success(emptyList())) }
            createViewModel()

            viewModel.onScrollPositionChanged(
                HomeCategory.TRENDING,
                lastVisibleIndex = threshold,
                totalItems = threshold * 2
            )

            val section = viewModel.state.value.trending as SectionState.Success
            assertTrue(section.hasReachedEnd)
        }

        @Test
        fun `GIVEN page 2 has duplicate items THEN duplicates are removed`() = runTest {
            val duplicate = Movie(1, "Duplicate", "/d.jpg", "/d.jpg", "Dup", 5.0, "2024-01-01")
            every { useCases.getTrendingMovies(2) } answers {
                flowOf(Result.Success(listOf(duplicate, movie2)))
            }
            createViewModel()

            viewModel.onScrollPositionChanged(
                HomeCategory.TRENDING,
                lastVisibleIndex = threshold,
                totalItems = threshold * 2
            )

            val section = viewModel.state.value.trending as SectionState.Success
            // movie (id=1) from page 1 + movie2 (id=3) from page 2; duplicate (id=1) filtered out
            assertEquals(2, section.items.size)
            assertEquals(listOf("movie_1", "movie_3"), section.items.map { it.uniqueId })
        }

        @Test
        fun `GIVEN page 2 errors THEN loadMoreState is Error with correct throwable`() = runTest {
            val error = RuntimeException("Network error")
            every { useCases.getTrendingMovies(2) } answers { flowOf(Result.Error(error)) }
            createViewModel()

            viewModel.onScrollPositionChanged(
                HomeCategory.TRENDING,
                lastVisibleIndex = threshold,
                totalItems = threshold * 2
            )

            val section = viewModel.state.value.trending as SectionState.Success
            val loadMoreState = section.loadMoreState as LoadMoreState.Error
            assertEquals(error, loadMoreState.throwable)
        }

        @Test
        fun `GIVEN page 2 errors THEN section remains Success with page 1 items intact`() =
            runTest {
                every { useCases.getTrendingMovies(2) } answers {
                    flowOf(
                        Result.Error(
                            RuntimeException()
                        )
                    )
                }
                createViewModel()

                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold,
                    totalItems = threshold * 2
                )

                val section = viewModel.state.value.trending as SectionState.Success
                assertEquals(1, section.items.size)
                assertEquals(1, section.page)
            }

        @Test
        fun `GIVEN loadMoreState is Error WHEN scroll at threshold THEN retries load`() = runTest {
            val error = RuntimeException()
            every { useCases.getTrendingMovies(2) } returnsMany listOf(
                flowOf(Result.Error(error)),
                flowOf(Result.Success(listOf(movie2))),
            )
            createViewModel()

            viewModel.onScrollPositionChanged(
                HomeCategory.TRENDING,
                lastVisibleIndex = threshold,
                totalItems = threshold * 2
            )
            viewModel.onScrollPositionChanged(
                HomeCategory.TRENDING,
                lastVisibleIndex = threshold,
                totalItems = threshold * 2
            )

            val section = viewModel.state.value.trending as SectionState.Success
            assertInstanceOf(LoadMoreState.Idle::class.java, section.loadMoreState)
            assertEquals(2, section.items.size)
        }

        @Test
        fun `GIVEN scroll below threshold WHEN onScrollPositionChanged THEN does not load more`() =
            runTest {
                createViewModel()

                // lastVisible is one position before the boundary → should not trigger
                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold - 1,
                    totalItems = threshold * 2
                )

                verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            }

        @Test
        fun `GIVEN totalItems is zero WHEN onScrollPositionChanged THEN does not load more`() =
            runTest {
                createViewModel()

                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = 0,
                    totalItems = 0
                )

                verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            }

        @Test
        fun `GIVEN totalItems at or below threshold WHEN onScrollPositionChanged THEN does not load eagerly`() =
            runTest {
                createViewModel()

                // totalItems == threshold → guard (totalItems > threshold) is false, so no load
                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold - 1,
                    totalItems = threshold
                )

                verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            }

        @Test
        fun `GIVEN section is not Success WHEN onScrollPositionChanged THEN does not load more`() =
            runTest {
                every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Loading) }
                createViewModel()

                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold,
                    totalItems = threshold * 2
                )

                verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            }

        @Test
        fun `GIVEN section is already loading more WHEN onScrollPositionChanged THEN does not load again`() =
            runTest {
                // page 2 returns Loading → loadMoreState stays Loading after first trigger
                every { useCases.getTrendingMovies(2) } answers { flowOf(Result.Loading) }
                createViewModel()

                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold,
                    totalItems = threshold * 2
                )
                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold,
                    totalItems = threshold * 2
                )

                // 1 call from init (page 1) + 1 call from first scroll (page 2) = 2 total; second scroll is blocked
                verify(exactly = 2) { useCases.getTrendingMovies(any()) }
            }

        @Test
        fun `GIVEN section has reached end WHEN onScrollPositionChanged THEN does not load more`() =
            runTest {
                every { useCases.getTrendingMovies(any()) } answers {
                    flowOf(
                        Result.Success(
                            emptyList()
                        )
                    )
                }
                createViewModel()

                viewModel.onScrollPositionChanged(
                    HomeCategory.TRENDING,
                    lastVisibleIndex = threshold,
                    totalItems = threshold * 2
                )

                verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            }
    }

    @Nested
    inner class Navigation {

        @Test
        fun `WHEN onItemClicked THEN emits NavigateToDetail event`() = runTest {
            createViewModel()

            viewModel.navEvents.test {
                viewModel.onItemClicked(1, "movie")
                assertEquals(HomeNavEvent.NavigateToDetail(1, "movie"), awaitItem())
            }
        }
    }

    @Nested
    inner class StateExtensions {

        @Test
        fun `WHEN forRefresh THEN resets all sections to Loading and sets isRefreshing`() {
            val state = HomeState(
                trending = SectionState.Error(Exception()),
                popularMovies = SectionState.Success(items = emptyList(), page = 1),
                popularTv = SectionState.Error(Exception()),
                topRatedMovies = SectionState.Loading,
                topRatedTv = SectionState.Success(items = emptyList(), page = 1),
            )

            val updated = state.forRefresh()

            assertTrue(updated.isRefreshing)
            assertEquals(SectionState.Loading, updated.trending)
            assertEquals(SectionState.Loading, updated.popularMovies)
            assertEquals(SectionState.Loading, updated.popularTv)
            assertEquals(SectionState.Loading, updated.topRatedMovies)
            assertEquals(SectionState.Loading, updated.topRatedTv)
        }

        @Test
        fun `WHEN sectionFor THEN returns the correct section for each category`() {
            val trending = SectionState.Success(emptyList(), page = 1)
            val popularMovies = SectionState.Error(Exception())
            val popularTv = SectionState.Loading
            val topRatedMovies = SectionState.Success(emptyList(), page = 2)
            val topRatedTv = SectionState.Error(Exception())
            val state = HomeState(
                trending = trending,
                popularMovies = popularMovies,
                popularTv = popularTv,
                topRatedMovies = topRatedMovies,
                topRatedTv = topRatedTv,
            )

            assertEquals(trending, state.sectionFor(HomeCategory.TRENDING))
            assertEquals(popularMovies, state.sectionFor(HomeCategory.POPULAR_MOVIES))
            assertEquals(popularTv, state.sectionFor(HomeCategory.POPULAR_TV))
            assertEquals(topRatedMovies, state.sectionFor(HomeCategory.TOP_RATED_MOVIES))
            assertEquals(topRatedTv, state.sectionFor(HomeCategory.TOP_RATED_TV))
        }

        @Test
        fun `WHEN updateSection THEN only the target section is changed`() {
            val newSection = SectionState.Error(Exception())
            val state = HomeState()

            HomeCategory.entries.forEach { category ->
                val updated = state.updateSection(category) { newSection }

                assertEquals(newSection, updated.sectionFor(category))
                HomeCategory.entries.filter { it != category }.forEach { other ->
                    assertEquals(state.sectionFor(other), updated.sectionFor(other))
                }
            }
        }
    }
}
