package com.klynaf.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.util.mapResult
import com.klynaf.feature.home.domain.usecase.HomeUseCases
import com.klynaf.uicore.model.MediaCardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: HomeUseCases,
    private val mapper: HomeUiModelMapper,
) : ViewModel() {

    companion object {
        internal const val LOAD_MORE_THRESHOLD = 3
    }

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<HomeNavEvent>()
    val navEvents: SharedFlow<HomeNavEvent> = _navEvents.asSharedFlow()

    init {
        loadAllCategories(isRefresh = false)
    }

    fun onRefresh() {
        if (_state.value.isRefreshing || _state.value.isAnySectionLoading) return
        _state.update { it.forRefresh() }
        loadAllCategories(isRefresh = true)
    }

    fun onRetry(category: HomeCategory) {
        when (val section = _state.value.sectionFor(category)) {
            is SectionState.Loading -> return
            is SectionState.Error -> viewModelScope.launch { loadPage(category, page = 1) }
            is SectionState.Success -> viewModelScope.launch {
                loadPage(
                    category,
                    page = section.page + 1
                )
            }
        }
    }

    fun onScrollPositionChanged(category: HomeCategory, lastVisibleIndex: Int, totalItems: Int) {
        if (totalItems > LOAD_MORE_THRESHOLD && lastVisibleIndex >= totalItems - LOAD_MORE_THRESHOLD) {
            loadMoreIfReady(category)
        }
    }

    private fun loadMoreIfReady(category: HomeCategory) {
        val section = _state.value.sectionFor(category)
        if (section !is SectionState.Success) return
        if (section.loadMoreState is LoadMoreState.Loading || section.hasReachedEnd) return
        viewModelScope.launch { loadPage(category, page = section.page + 1) }
    }

    fun onItemClicked(mediaId: Int, mediaTypeRoute: String) {
        viewModelScope.launch {
            _navEvents.emit(HomeNavEvent.NavigateToDetail(mediaId, mediaTypeRoute))
        }
    }

    private fun loadAllCategories(isRefresh: Boolean) {
        viewModelScope.launch {
            val jobs = HomeCategory.entries.map { category ->
                launch { loadPage(category = category, page = 1) }
            }
            jobs.joinAll()
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private suspend fun loadPage(category: HomeCategory, page: Int) {
        _state.update { state ->
            state.updateSection(category) { section ->
                if (page == 1) {
                    SectionState.Loading
                } else when (section) {
                    is SectionState.Success -> section.copy(loadMoreState = LoadMoreState.Loading)
                    else -> SectionState.Loading
                }
            }
        }

        flowForCategory(category, page).collect { result ->
            when (result) {
                is Result.Loading -> Unit
                is Result.Success -> {
                    val newItems = result.data
                    _state.update { state ->
                        state.updateSection(category) { section ->
                            if (page == 1) {
                                SectionState.Success(
                                    items = newItems,
                                    page = 1,
                                    hasReachedEnd = newItems.isEmpty(),
                                )
                            } else when (section) {
                                is SectionState.Success -> section.copy(
                                    items = (section.items + newItems).distinctBy { it.uniqueId },
                                    page = page,
                                    loadMoreState = LoadMoreState.Idle,
                                    hasReachedEnd = newItems.isEmpty(),
                                )

                                else -> SectionState.Success(
                                    items = newItems,
                                    page = page,
                                    hasReachedEnd = newItems.isEmpty(),
                                )
                            }
                        }
                    }
                }

                is Result.Error -> {
                    _state.update { state ->
                        state.updateSection(category) { section ->
                            if (page == 1) {
                                SectionState.Error(result.throwable)
                            } else when (section) {
                                is SectionState.Success -> section.copy(
                                    loadMoreState = LoadMoreState.Error(result.throwable),
                                )

                                else -> SectionState.Error(result.throwable)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun flowForCategory(
        category: HomeCategory,
        page: Int,
    ): Flow<Result<List<MediaCardUiModel>>> = when (category) {
        HomeCategory.TRENDING -> useCases.getTrendingMovies(page).map { result ->
            result.mapResult { list -> list.map { mapper.mapMovie(it) } }
        }

        HomeCategory.POPULAR_MOVIES -> useCases.getPopularMovies(page).map { result ->
            result.mapResult { list -> list.map { mapper.mapMovie(it) } }
        }

        HomeCategory.POPULAR_TV -> useCases.getPopularTv(page).map { result ->
            result.mapResult { list -> list.map { mapper.mapTvShow(it) } }
        }

        HomeCategory.TOP_RATED_MOVIES -> useCases.getTopRatedMovies(page).map { result ->
            result.mapResult { list -> list.map { mapper.mapMovie(it) } }
        }

        HomeCategory.TOP_RATED_TV -> useCases.getTopRatedTv(page).map { result ->
            result.mapResult { list -> list.map { mapper.mapTvShow(it) } }
        }
    }
}
