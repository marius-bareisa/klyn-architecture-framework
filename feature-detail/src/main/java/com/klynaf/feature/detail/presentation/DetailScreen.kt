package com.klynaf.feature.detail.presentation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klynaf.core.domain.util.Result
import com.klynaf.uicore.R
import com.klynaf.uicore.components.CastShimmerRow
import com.klynaf.uicore.components.ErrorRow
import com.klynaf.uicore.components.ErrorState
import com.klynaf.uicore.components.LoadingState
import com.klynaf.uicore.components.MediaCard
import com.klynaf.uicore.components.MediaShimmerRow
import com.klynaf.uicore.components.KlynAFAsyncImage
import com.klynaf.uicore.components.TrailerShimmerRow
import com.klynaf.uicore.components.WatchlistToggleButton
import com.klynaf.uicore.theme.Dimens
import com.klynaf.uicore.util.ObserveNavEvents
import com.klynaf.uicore.util.toUserMessage

@Stable
class DetailActions(
    val onNavigateBack: () -> Unit,
    val onWatchlistToggled: () -> Unit,
    val onRefresh: () -> Unit,
    val onRetry: (DetailSection) -> Unit,
    val onTrailerClicked: (TrailerUiModel) -> Unit,
    val onSimilarItemClicked: (Int, String) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int, String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()

    val msgAdded = stringResource(R.string.detail_snackbar_added_watchlist)
    val msgRemoved = stringResource(R.string.detail_snackbar_removed_watchlist)

    val actions = remember(viewModel, onNavigateBack, onNavigateToDetail) {
        DetailActions(
            onNavigateBack = onNavigateBack,
            onWatchlistToggled = viewModel::onWatchlistToggled,
            onRefresh = viewModel::onRefresh,
            onRetry = viewModel::onRetry,
            onTrailerClicked = viewModel::onTrailerClicked,
            onSimilarItemClicked = viewModel::onSimilarItemClicked
        )
    }

    ObserveNavEvents(viewModel.navEvents) { event ->
        when (event) {
            is DetailNavEvent.TrailerClicked -> {
                context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
            }

            is DetailNavEvent.NavigateToDetail -> onNavigateToDetail(event.mediaId, event.mediaType)
            is DetailNavEvent.ShowWatchlistSnackbar -> {
                val message = if (event.action == WatchlistAction.ADDED) msgAdded else msgRemoved
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    DetailContent(
        state = state,
        actions = actions,
        pullToRefreshState = pullToRefreshState,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
    state: DetailState,
    actions: DetailActions,
    pullToRefreshState: PullToRefreshState,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DetailTopAppBar(
                title = state.title,
                isLoading = state.media is Result.Loading && !state.isRefreshing,
                isWatchlisted = state.isWatchlisted,
                onBack = actions.onNavigateBack,
                onWatchlistToggle = actions.onWatchlistToggled
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullToRefresh(
                    state = pullToRefreshState,
                    isRefreshing = state.isRefreshing,
                    onRefresh = actions.onRefresh,
                    enabled = !state.isAnySectionLoading
                )
        ) {
            when (val mediaResult = state.media) {
                is Result.Loading -> if (!state.isRefreshing) LoadingState(Modifier.fillMaxSize())
                is Result.Error -> ErrorState(
                    message = mediaResult.throwable.toUserMessage(LocalContext.current),
                    onRetry = { actions.onRetry(DetailSection.MEDIA) },
                    modifier = Modifier.fillMaxSize()
                )

                is Result.Success -> DetailSuccessContent(
                    media = mediaResult.data,
                    state = state,
                    actions = actions
                )
            }

            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun DetailSuccessContent(
    media: DetailUiModel,
    state: DetailState,
    actions: DetailActions,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            KlynAFAsyncImage(
                imageUrl = media.backdropUrl,
                contentDescription = stringResource(
                    R.string.detail_desc_backdrop_format,
                    media.title
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )
        }

        item { MediaHeaderSection(media) }

        renderSection(
            titleRes = R.string.detail_title_cast,
            result = state.cast,
            shimmer = { CastShimmerRow() },
            onRetry = { actions.onRetry(DetailSection.CAST) }
        ) { castList ->
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .height(IntrinsicSize.Max)
                    .padding(horizontal = Dimens.SpacingMedium),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
            ) {
                castList.forEach { cast ->
                    CastItem(
                        cast = cast,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
        }

        renderSection(
            titleRes = R.string.detail_title_trailers,
            result = state.trailers,
            shimmer = { TrailerShimmerRow() },
            onRetry = { actions.onRetry(DetailSection.TRAILERS) }
        ) { trailers ->
            LazyRow(
                contentPadding = PaddingValues(horizontal = Dimens.SpacingMedium),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
            ) {
                items(trailers, key = { it.videoUrl }) { trailer ->
                    TrailerCard(trailer, onClick = { actions.onTrailerClicked(trailer) })
                }
            }
        }

        renderSection(
            titleRes = R.string.detail_title_similar,
            result = state.similar,
            shimmer = { MediaShimmerRow() },
            onRetry = { actions.onRetry(DetailSection.SIMILAR) }
        ) { similar ->
            LazyRow(
                contentPadding = PaddingValues(horizontal = Dimens.SpacingMedium),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
            ) {
                items(similar, key = { it.uniqueId }) { item ->
                    MediaCard(
                        posterUrl = item.posterUrl,
                        title = item.title,
                        voteAverage = item.voteAverage,
                        onClick = { actions.onSimilarItemClicked(item.mediaId, item.mediaTypeRoute) },
                        modifier = Modifier.width(Dimens.PosterWidthLarge)
                    )
                }
            }
        }
    }
}

private fun <T> LazyListScope.renderSection(
    titleRes: Int,
    result: Result<List<T>>,
    shimmer: @Composable () -> Unit,
    onRetry: () -> Unit,
    content: @Composable (List<T>) -> Unit
) {
    item { SectionHeader(stringResource(titleRes)) }
    item {
        when (result) {
            is Result.Loading -> shimmer()
            is Result.Error -> ErrorRow(
                message = result.throwable.toUserMessage(LocalContext.current),
                onRetry = onRetry
            )

            is Result.Success -> if (result.data.isEmpty()) EmptyRow() else content(result.data)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopAppBar(
    title: String,
    isLoading: Boolean,
    isWatchlisted: Boolean,
    onBack: () -> Unit,
    onWatchlistToggle: () -> Unit
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = if (isLoading) "" else title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                WatchlistToggleButton(
                    isWatchlisted = isWatchlisted,
                    onClick = onWatchlistToggle
                )
            }
        )
        if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MediaHeaderSection(media: DetailUiModel) {
    Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
        Text(text = media.title, style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = media.releaseYear, style = MaterialTheme.typography.bodyMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = media.formattedRating,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(Modifier.height(Dimens.SpacingSmall))
        Text(text = media.overview, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CastItem(
    cast: CastUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KlynAFAsyncImage(
            imageUrl = cast.profileUrl,
            contentDescription = cast.name,
            fallbackIcon = Icons.Default.Person,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(Dimens.SpacingSmall))

        Text(
            text = cast.name,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(Dimens.SpacingExtraSmall))

        Text(
            text = cast.character,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TrailerCard(trailer: TrailerUiModel, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.width(Dimens.TrailerCardWidth)) {
        Box {
            KlynAFAsyncImage(
                imageUrl = trailer.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(Dimens.PlayIconSize)
            )
        }
        Text(
            text = trailer.name,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(Dimens.SpacingSmall),
            maxLines = 2
        )
    }
}

@Composable
private fun SectionHeader(title: String) = Text(
    text = title,
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier.padding(Dimens.SpacingMedium, Dimens.SpacingSmall)
)

@Composable
private fun EmptyRow() = Text(
    text = stringResource(R.string.detail_label_not_available),
    style = MaterialTheme.typography.bodySmall,
    modifier = Modifier.padding(horizontal = Dimens.SpacingMedium)
)
