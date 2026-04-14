package com.klynaf.feature.search.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klynaf.uicore.R
import com.klynaf.uicore.components.EmptyState
import com.klynaf.uicore.components.ErrorState
import com.klynaf.uicore.components.LoadingState
import com.klynaf.uicore.components.PosterImage
import com.klynaf.uicore.model.MediaTypeUi
import com.klynaf.uicore.theme.Dimens
import com.klynaf.uicore.util.ObserveNavEvents
import com.klynaf.uicore.util.toUserMessage

@Composable
fun SearchScreen(
    onNavigateToDetail: (Int, String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveNavEvents(viewModel.navEvents) { event ->
        when (event) {
            is SearchNavEvent.NavigateToDetail -> onNavigateToDetail(
                event.mediaId,
                event.mediaType
            )
        }
    }

    SearchContent(
        state = state,
        onQueryChanged = viewModel::onQueryChanged,
        onQueryCleared = viewModel::onQueryCleared,
        onItemClicked = viewModel::onItemClicked,
        onRetry = viewModel::onRetry,
    )
}

@Composable
private fun SearchContent(
    state: SearchState,
    onQueryChanged: (String) -> Unit,
    onQueryCleared: () -> Unit,
    onItemClicked: (Int, String) -> Unit,
    onRetry: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChanged,
            placeholder = { Text(stringResource(R.string.search_label_input_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(onClick = onQueryCleared) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.search_action_clear)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingSmall),
        )

        when {
            state.isLoading -> LoadingState()

            state.errorThrowable != null -> ErrorState(
                message = state.errorThrowable.toUserMessage(LocalContext.current),
                onRetry = onRetry,
            )

            state.hasNoResults -> EmptyState(
                message = stringResource(
                    R.string.search_label_no_results_format,
                    state.query
                )
            )

            else -> LazyColumn {
                items(state.items, key = { it.uniqueId }) { uiModel ->
                    MediaListItem(
                        uiModel = uiModel,
                        onClick = { onItemClicked(uiModel.mediaId, uiModel.mediaTypeRoute) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaListItem(
    uiModel: SearchResultUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val typeLabel = stringResource(
        when (uiModel.mediaTypeUi) {
            MediaTypeUi.Movie -> R.string.shared_label_movie
            MediaTypeUi.TvShow -> R.string.shared_label_tv
        }
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = stringResource(R.string.shared_action_view_details),
                onClick = onClick
            )
            .semantics { role = Role.Button }
            .padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PosterImage(
            url = uiModel.posterUrl,
            modifier = Modifier.size(Dimens.PosterWidthMedium, Dimens.PosterHeightMedium),
            contentDescription = "${uiModel.title} ($typeLabel)"
        )
        Spacer(Modifier.width(Dimens.SpacingMediumSmall))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = uiModel.title, style = MaterialTheme.typography.bodyLarge)
            Row {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = uiModel.year,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(
                            horizontal = Dimens.SpacingChipHorizontal,
                            vertical = Dimens.SpacingChipVertical
                        )
                    )
                }
                Spacer(Modifier.width(Dimens.SpacingSmall))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(
                            horizontal = Dimens.SpacingChipHorizontal,
                            vertical = Dimens.SpacingChipVertical
                        )
                    )
                }
            }
        }
    }
}
