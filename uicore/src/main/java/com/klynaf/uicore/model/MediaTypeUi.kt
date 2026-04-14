package com.klynaf.uicore.model

sealed interface MediaTypeUi {
    data object Movie : MediaTypeUi
    data object TvShow : MediaTypeUi
}
