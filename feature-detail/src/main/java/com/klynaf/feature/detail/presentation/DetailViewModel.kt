package com.klynaf.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.core.domain.util.Result
import com.klynaf.core.domain.util.mapResult
import com.klynaf.core.util.TimeProvider
import com.klynaf.feature.detail.domain.usecase.DetailUseCases
import com.klynaf.uicore.model.MediaCardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

enum class DetailSection { MEDIA, CAST, TRAILERS, SIMILAR }

data class DetailState(
    val media: Result<DetailUiModel> = Result.Loading,
    val cast: Result<List<CastUiModel>> = Result.Loading,
    val trailers: Result<List<TrailerUiModel>> = Result.Loading,
    val similar: Result<List<MediaCardUiModel>> = Result.Loading,
    val isWatchlisted: Boolean = false,
    val isRefreshing: Boolean = false,
) {
    val title: String
        get() = (media as? Result.Success)?.data?.title ?: ""

    val isAnySectionLoading: Boolean
        get() = media is Result.Loading ||
                cast is Result.Loading ||
                trailers is Result.Loading ||
                similar is Result.Loading
}

enum class WatchlistAction { ADDED, REMOVED }

sealed interface DetailNavEvent {
    data class TrailerClicked(val url: String) : DetailNavEvent
    data class NavigateToDetail(val mediaId: Int, val mediaType: String) : DetailNavEvent
    data class ShowWatchlistSnackbar(val action: WatchlistAction) : DetailNavEvent
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: DetailUseCases,
    private val timeProvider: TimeProvider,
    private val mapper: DetailUiModelMapper,
) : ViewModel() {

    private val mediaId: Int = checkNotNull(savedStateHandle["mediaId"])
    private val mediaTypeStr: String = checkNotNull(savedStateHandle["mediaType"])
    private val mediaType: MediaType = MediaType.fromRoute(mediaTypeStr)

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<DetailNavEvent>()
    val navEvents: SharedFlow<DetailNavEvent> = _navEvents.asSharedFlow()

    private var watchlistToggleJob: Job? = null

    init {
        loadDetail()
        observeWatchlist()
    }

    fun onRefresh() {
        if (_state.value.isRefreshing || _state.value.isAnySectionLoading) return

        _state.update { it.clearErrorsForRefresh() }
        loadDetail(isRefresh = true)
    }

    fun onRetry(section: DetailSection = DetailSection.MEDIA) {
        if (_state.value.isRefreshing) return
        loadSection(section, isRefresh = false)
    }

    fun onWatchlistToggled() {
        watchlistToggleJob?.cancel()
        watchlistToggleJob = viewModelScope.launch {
            val mediaResult = _state.value.media
            if (mediaResult !is Result.Success) return@launch

            val media = mediaResult.data
            val item = WatchlistItem(
                mediaId, mediaType, media.title, media.posterPath, timeProvider.now()
            )

            val wasWatchlisted = _state.value.isWatchlisted
            useCases.toggleWatchlist(item)
            _navEvents.emit(
                DetailNavEvent.ShowWatchlistSnackbar(
                    if (wasWatchlisted) WatchlistAction.REMOVED else WatchlistAction.ADDED
                )
            )
        }
    }

    fun onTrailerClicked(trailer: TrailerUiModel) {
        viewModelScope.launch {
            _navEvents.emit(DetailNavEvent.TrailerClicked(trailer.videoUrl))
        }
    }

    fun onSimilarItemClicked(mediaId: Int, mediaTypeRoute: String) {
        viewModelScope.launch {
            _navEvents.emit(
                DetailNavEvent.NavigateToDetail(
                    mediaId, mediaTypeRoute
                )
            )
        }
    }

    private fun loadDetail(isRefresh: Boolean = false) {
        loadSection(DetailSection.MEDIA, isRefresh)
    }

    private fun loadSection(section: DetailSection, isRefresh: Boolean) {
        viewModelScope.launch {
            if (section == DetailSection.MEDIA) {
                val jobs = listOf(
                    launch { fetchMedia(isRefresh) },
                    launch { fetchCast(isRefresh) },
                    launch { fetchTrailers(isRefresh) },
                    launch { fetchSimilar(isRefresh) }
                )
                jobs.joinAll()
                _state.update { it.copy(isRefreshing = false) }
            } else {
                when (section) {
                    DetailSection.CAST -> fetchCast(isRefresh)
                    DetailSection.TRAILERS -> fetchTrailers(isRefresh)
                    DetailSection.SIMILAR -> fetchSimilar(isRefresh)
                }
            }
        }
    }

    private suspend fun fetchMedia(isRefresh: Boolean) {
        val flow = useCases.getMediaDetail(mediaId, mediaType).map { res ->
            res.mapResult { item ->
                when (item) {
                    is MediaItem.MovieItem -> mapper.mapMovie(item.movie)
                    is MediaItem.TvItem -> mapper.mapTvShow(item.tvShow)
                }
            }
        }
        fetchSection(flow, isRefresh) { state, res -> state.copy(media = res) }
    }

    private suspend fun fetchCast(isRefresh: Boolean) {
        val flow = useCases.getCredits(mediaId, mediaType).map { res ->
            res.mapResult { list -> list.map { mapper.mapCast(it) } }
        }
        fetchSection(flow, isRefresh) { state, res -> state.copy(cast = res) }
    }

    private suspend fun fetchTrailers(isRefresh: Boolean) {
        val flow = useCases.getTrailers(mediaId, mediaType).map { res ->
            res.mapResult { list -> list.map { mapper.mapTrailer(it) } }
        }
        fetchSection(flow, isRefresh) { state, res -> state.copy(trailers = res) }
    }

    private suspend fun fetchSimilar(isRefresh: Boolean) {
        val flow = useCases.getSimilar(mediaId, mediaType).map { res ->
            res.mapResult { list -> list.map { mapper.mapMediaItem(it) } }
        }
        fetchSection(flow, isRefresh) { state, res -> state.copy(similar = res) }
    }

    private suspend inline fun <T> fetchSection(
        flow: Flow<Result<T>>,
        isRefresh: Boolean,
        crossinline stateUpdater: (DetailState, Result<T>) -> DetailState,
    ) {
        if (!isRefresh) {
            _state.update { stateUpdater(it, Result.Loading) }
        }

        flow.collect { result ->
            if (isRefresh && result is Result.Loading) return@collect
            _state.update { stateUpdater(it, result) }
        }
    }

    private fun observeWatchlist() {
        viewModelScope.launch {
            useCases.getWatchlistStatus(mediaId, mediaType).collect { isWatchlisted ->
                _state.update { it.copy(isWatchlisted = isWatchlisted) }
            }
        }
    }
}
