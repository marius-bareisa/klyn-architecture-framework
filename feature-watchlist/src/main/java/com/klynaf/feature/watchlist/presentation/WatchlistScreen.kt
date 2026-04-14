package com.klynaf.feature.watchlist.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klynaf.uicore.R
import com.klynaf.uicore.components.EmptyState
import com.klynaf.uicore.components.PosterImage
import com.klynaf.uicore.components.WatchlistStar
import com.klynaf.uicore.model.MediaTypeUi
import com.klynaf.uicore.theme.Dimens
import com.klynaf.uicore.util.ObserveNavEvents

@Composable
fun WatchlistScreen(
    onNavigateToDetail: (Int, String) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveNavEvents(viewModel.navEvents) { event ->
        when (event) {
            is WatchlistNavEvent.NavigateToDetail -> onNavigateToDetail(
                event.mediaId,
                event.mediaType
            )
        }
    }

    WatchlistContent(
        state = state,
        onItemClicked = viewModel::onItemClicked,
        onItemRemoved = viewModel::onItemRemoved
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistContent(
    state: WatchlistState,
    onItemClicked: (WatchlistUiModel) -> Unit,
    onItemRemoved: (WatchlistUiModel) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.watchlist_title_screen)) }) }
    ) { paddingValues ->
        when {
            state.isEmpty -> EmptyState(
                message = stringResource(R.string.watchlist_message_empty_state),
                graphic = {
                    WatchlistStar(
                        isWatchlisted = false,
                        iconSize = 64.dp
                    )
                },
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(state.items, key = { "${it.mediaId}_${it.mediaTypeRoute}" }) { item ->
                    SwipeToDismissItem(
                        item = item,
                        modifier = Modifier.animateItem(),
                        onDismiss = { onItemRemoved(item) },
                        onClick = { onItemClicked(item) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissItem(
    item: WatchlistUiModel,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    val isAtThreshold = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart

    val iconScale by animateFloatAsState(
        targetValue = if (isAtThreshold) 1.2f else 1.0f,
        label = "SwipeIconScale"
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDismiss()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error)
                    .padding(end = Dimens.SpacingMedium),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.watchlist_action_remove),
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.scale(iconScale)
                )
            }
        },
        content = {
            WatchlistItemRow(item = item, onClick = onClick)
        }
    )
}

@Composable
private fun WatchlistItemRow(
    item: WatchlistUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                onClickLabel = stringResource(R.string.shared_action_view_details),
                onClick = onClick
            )
            .semantics { role = Role.Button }
            .padding(Dimens.SpacingMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PosterImage(
            url = item.posterUrl,
            modifier = Modifier.size(Dimens.PosterWidthSmall, Dimens.PosterHeightSmall)
        )
        Spacer(Modifier.width(Dimens.SpacingMediumSmall))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
            val typeLabel = stringResource(
                when (item.mediaTypeUi) {
                    MediaTypeUi.Movie -> R.string.shared_label_movie
                    MediaTypeUi.TvShow -> R.string.shared_label_tv_show
                }
            )
            Text(
                text = typeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
