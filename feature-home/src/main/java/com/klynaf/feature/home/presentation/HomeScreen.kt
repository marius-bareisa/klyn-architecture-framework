package com.klynaf.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klynaf.core.domain.util.Result
import com.klynaf.uicore.R
import com.klynaf.uicore.components.ErrorRow
import com.klynaf.uicore.components.MediaCard
import com.klynaf.uicore.components.MediaShimmerRow
import com.klynaf.uicore.model.MediaCardUiModel
import com.klynaf.uicore.theme.Dimens
import com.klynaf.uicore.util.ObserveNavEvents
import com.klynaf.uicore.util.toUserMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    ObserveNavEvents(viewModel.navEvents) { event ->
        when (event) {
            is HomeNavEvent.NavigateToDetail -> onNavigateToDetail(
                event.mediaId,
                event.mediaType
            )
        }
    }

    HomeContent(
        state = state,
        pullToRefreshState = pullToRefreshState,
        onRefresh = viewModel::onRefresh,
        onItemClicked = viewModel::onItemClicked,
        onRetry = viewModel::onRetry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeState,
    pullToRefreshState: PullToRefreshState,
    onRefresh: () -> Unit,
    onItemClicked: (Int, String) -> Unit,
    onRetry: (HomeCategory) -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text(stringResource(R.string.shared_label_app_name)) })
                if (state.isAnySectionLoading && !state.isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullToRefresh(
                    state = pullToRefreshState,
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                    enabled = !state.isAnySectionLoading
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = Dimens.SpacingMedium)
            ) {
                item {
                    HomeSection(
                        title = stringResource(R.string.home_title_trending),
                        state = state.trending,
                        onItemClick = onItemClicked,
                        onRetry = { onRetry(HomeCategory.TRENDING) }
                    )
                }
                item { Spacer(modifier = Modifier.height(Dimens.SpacingMedium)) }
                item {
                    HomeSection(
                        title = stringResource(R.string.home_title_popular_movies),
                        state = state.popularMovies,
                        onItemClick = onItemClicked,
                        onRetry = { onRetry(HomeCategory.POPULAR_MOVIES) }
                    )
                }
                item { Spacer(modifier = Modifier.height(Dimens.SpacingMedium)) }
                item {
                    HomeSection(
                        title = stringResource(R.string.home_title_popular_tv),
                        state = state.popularTv,
                        onItemClick = onItemClicked,
                        onRetry = { onRetry(HomeCategory.POPULAR_TV) }
                    )
                }
                item { Spacer(modifier = Modifier.height(Dimens.SpacingMedium)) }
                item {
                    HomeSection(
                        title = stringResource(R.string.home_title_top_rated_movies),
                        state = state.topRatedMovies,
                        onItemClick = onItemClicked,
                        onRetry = { onRetry(HomeCategory.TOP_RATED_MOVIES) }
                    )
                }
                item { Spacer(modifier = Modifier.height(Dimens.SpacingMedium)) }
                item {
                    HomeSection(
                        title = stringResource(R.string.home_title_top_rated_tv),
                        state = state.topRatedTv,
                        onItemClick = onItemClicked,
                        onRetry = { onRetry(HomeCategory.TOP_RATED_TV) }
                    )
                }
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
private fun HomeSection(
    title: String,
    state: Result<List<MediaCardUiModel>>,
    onItemClick: (mediaId: Int, mediaTypeRoute: String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(
                horizontal = Dimens.SpacingMedium,
                vertical = Dimens.SpacingSmall
            ),
        )

        when (state) {
            is Result.Loading -> MediaShimmerRow()
            is Result.Success -> ContentRow(items = state.data, onItemClick = onItemClick)
            is Result.Error -> ErrorRow(
                message = state.throwable.toUserMessage(LocalContext.current),
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun ContentRow(
    items: List<MediaCardUiModel>,
    onItemClick: (mediaId: Int, mediaTypeRoute: String) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.SpacingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
    ) {
        items(items, key = { it.uniqueId }) { uiModel ->
            MediaCard(
                posterUrl = uiModel.posterUrl,
                title = uiModel.title,
                voteAverage = uiModel.voteAverage,
                onClick = { onItemClick(uiModel.mediaId, uiModel.mediaTypeRoute) },
                modifier = Modifier.width(Dimens.PosterWidthLarge),
            )
        }
    }
}
