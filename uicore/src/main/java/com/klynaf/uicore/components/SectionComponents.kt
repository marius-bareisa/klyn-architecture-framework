package com.klynaf.uicore.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.klynaf.uicore.R
import com.klynaf.uicore.theme.Dimens

@Composable
fun MediaShimmerRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.SpacingMedium),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.SpacingSmall),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        userScrollEnabled = false
    ) {
        items(5) {
            MediaCardShimmer(
                modifier = Modifier.width(Dimens.PosterWidthLarge)
            )
        }
    }
}

@Composable
fun CastShimmerRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.SpacingMedium),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.SpacingMediumSmall),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        userScrollEnabled = false
    ) {
        items(5) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(Dimens.CastItemWidth)
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(Dimens.AvatarSize)
                        .clip(CircleShape)
                )
                Box(Modifier.height(Dimens.SpacingExtraSmall))
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(Dimens.SpacingSmall)
                )
            }
        }
    }
}

@Composable
fun TrailerShimmerRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.SpacingMedium),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimens.SpacingSmall),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        userScrollEnabled = false
    ) {
        items(3) {
            Card(modifier = Modifier.width(Dimens.TrailerCardWidth)) {
                Column {
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )
                    Box(Modifier.height(Dimens.SpacingSmall))
                    ShimmerEffect(
                        modifier = Modifier
                            .padding(Dimens.SpacingSmall)
                            .fillMaxWidth(0.7f)
                            .height(Dimens.SpacingSmall)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorRow(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = Dimens.SpacingMedium),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.core_retry))
        }
    }
}
