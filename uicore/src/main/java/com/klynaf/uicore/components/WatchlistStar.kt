package com.klynaf.uicore.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.klynaf.uicore.R


@Composable
fun WatchlistStar(
    isWatchlisted: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp
) {
    val starColor by animateColorAsState(
        targetValue = if (isWatchlisted) {
            MaterialTheme.colorScheme.tertiary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        },
        label = "WatchlistStarColor"
    )

    val starScale by animateFloatAsState(
        targetValue = if (isWatchlisted) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "WatchlistStarScale"
    )

    val starRotation by animateFloatAsState(
        targetValue = if (isWatchlisted) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "WatchlistStarRotation"
    )

    Icon(
        imageVector = if (isWatchlisted) Icons.Filled.Star else Icons.Outlined.Star,
        contentDescription = null,
        tint = starColor,
        modifier = modifier
            .size(iconSize)
            .graphicsLayer {
                scaleX = starScale
                scaleY = starScale
                rotationZ = starRotation
            }
    )
}

@Composable
fun WatchlistToggleButton(
    isWatchlisted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = if (isWatchlisted)
        stringResource(R.string.detail_button_remove_watchlist)
    else
        stringResource(R.string.detail_button_add_watchlist)

    IconButton(onClick = onClick, modifier = modifier) {
        WatchlistStar(
            isWatchlisted = isWatchlisted,
            modifier = Modifier.semantics { contentDescription = description }
        )
    }
}
