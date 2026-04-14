package com.klynaf.uicore.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.klynaf.uicore.theme.Dimens

internal object MediaCardTokens {
    const val ASPECT_RATIO = 2f / 3f

    val ContentPadding = Dimens.SpacingSmall
    val TextSpacing = Dimens.SpacingExtraSmall

    val RatingPillMargin = 4.dp
    val RatingTextPadHorizontal = 4.dp
    val RatingTextPadVertical = 2.dp

    val ShimmerTextHeight = 12.dp
    val ShimmerRadius = 4.dp
    const val SHIMMER_LINE_ONE_WIDTH = 0.8f
    const val SHIMMER_LINE_TWO_WIDTH = 0.5f
}

@Composable
internal fun BaseMediaCard(
    modifier: Modifier = Modifier,
    imageContent: @Composable BoxScope.() -> Unit,
    textContent: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = modifier) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(MediaCardTokens.ASPECT_RATIO),
                content = imageContent
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MediaCardTokens.ContentPadding),
                verticalArrangement = Arrangement.spacedBy(MediaCardTokens.TextSpacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = textContent
            )
        }
    }
}

@Composable
fun MediaCard(
    posterUrl: String?,
    title: String,
    voteAverage: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseMediaCard(
        modifier = modifier
            .semantics {
                role = Role.Button
                contentDescription = "$title, rated ${"%.1f".format(voteAverage)}"
            }
            .clickable(onClick = onClick),
        imageContent = {
            PosterImage(
                url = posterUrl,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MediaCardTokens.RatingPillMargin),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "%.1f".format(voteAverage),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(
                        horizontal = MediaCardTokens.RatingTextPadHorizontal,
                        vertical = MediaCardTokens.RatingTextPadVertical
                    )
                )
            }
        },
        textContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    minLines = 2,
                    maxLines = 2,
                    modifier = Modifier.clearAndSetSemantics { }
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun MediaCardShimmer(modifier: Modifier = Modifier) {
    BaseMediaCard(
        modifier = modifier,
        imageContent = {
            ShimmerEffect(modifier = Modifier.fillMaxSize())
        },
        textContent = {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(MediaCardTokens.SHIMMER_LINE_ONE_WIDTH)
                    .height(MediaCardTokens.ShimmerTextHeight)
                    .clip(RoundedCornerShape(MediaCardTokens.ShimmerRadius))
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(MediaCardTokens.SHIMMER_LINE_TWO_WIDTH)
                    .height(MediaCardTokens.ShimmerTextHeight)
                    .clip(RoundedCornerShape(MediaCardTokens.ShimmerRadius))
            )
        }
    )
}
