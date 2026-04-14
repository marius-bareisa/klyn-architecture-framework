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

    private val movie = Movie(1, "Movie", "/p.jpg", "/b.jpg", "Overview", 8.0, "2024-01-01")
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
        fun `WHEN init THEN state is loaded correctly`() = runTest {
            createViewModel()

            val state = viewModel.state.value
            assertTrue(state.trending is Result.Success)
            assertTrue(state.popularMovies is Result.Success)
            assertTrue(state.popularTv is Result.Success)
            assertTrue(state.topRatedMovies is Result.Success)
            assertTrue(state.topRatedTv is Result.Success)
            assertFalse(state.isRefreshing)
        }

        @Test
        fun `GIVEN use cases return loading WHEN init THEN state sections are loading`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Loading) }
            createViewModel()

            assertTrue(viewModel.state.value.trending is Result.Loading)
            assertTrue(viewModel.state.value.isAnySectionLoading)
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
        fun `GIVEN isRefreshing true WHEN onRefresh THEN does nothing`() = runTest {
            every { useCases.getTrendingMovies(any()) } answers { flowOf(Result.Loading) }
            createViewModel()

            viewModel.onRefresh()

            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
        }
    }

    @Nested
    inner class Retry {

        @Test
        fun `WHEN onRetry TRENDING THEN reloads only trending`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.TRENDING)

            verify(exactly = 2) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularMovies(any()) }
        }

        @Test
        fun `WHEN onRetry POPULAR_MOVIES THEN reloads only popular movies`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.POPULAR_MOVIES)

            verify(exactly = 2) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
        }

        @Test
        fun `WHEN onRetry POPULAR_TV THEN reloads only popularTv`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.POPULAR_TV)

            verify(exactly = 2) { useCases.getPopularTv(any()) }
            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getTopRatedMovies(any()) }
            verify(exactly = 1) { useCases.getTopRatedTv(any()) }
        }

        @Test
        fun `WHEN onRetry TOP_RATED_MOVIES THEN reloads only topRatedMovies`() = runTest {
            createViewModel()

            viewModel.onRetry(HomeCategory.TOP_RATED_MOVIES)

            verify(exactly = 2) { useCases.getTopRatedMovies(any()) }
            verify(exactly = 1) { useCases.getTrendingMovies(any()) }
            verify(exactly = 1) { useCases.getPopularMovies(any()) }
            verify(exactly = 1) { useCases.getPopularTv(any()) }
            verify(exactly = 1) { useCases.getTopRatedTv(any()) }
        }

        @Test
        fun `WHEN onRetry TOP_RATED_TV THEN reloads only topRatedTv`() = runTest {
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
    inner class Navigation {

        @Test
        fun `WHEN onItemClicked THEN emits event`() = runTest {
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
        fun `WHEN clearErrorsForRefresh THEN resets errors to Loading but keeps Success`() {
            val state = HomeState(
                trending = Result.Error(Exception()),
                popularMovies = Result.Success(emptyList()),
                popularTv = Result.Error(Exception()),
                topRatedMovies = Result.Loading,
                topRatedTv = Result.Success(emptyList())
            )

            val updated = state.clearErrorsForRefresh()

            assertTrue(updated.isRefreshing)
            assertTrue(updated.trending is Result.Loading)
            assertTrue(updated.popularMovies is Result.Success)
            assertTrue(updated.popularTv is Result.Loading)
            assertTrue(updated.topRatedMovies is Result.Loading)
            assertTrue(updated.topRatedTv is Result.Success)
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
                assertTrue(state.trending is Result.Error)
                assertTrue(state.popularMovies is Result.Success)
                assertTrue(state.popularTv is Result.Success)
                assertTrue(state.topRatedMovies is Result.Success)
                assertTrue(state.topRatedTv is Result.Success)
            }
    }
}
