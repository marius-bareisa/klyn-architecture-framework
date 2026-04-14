package com.klynaf.uicore.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PosterImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
) {
    KlynAFAsyncImage(
        imageUrl = url,
        contentDescription = contentDescription.ifEmpty { null },
        modifier = modifier
    )
}
