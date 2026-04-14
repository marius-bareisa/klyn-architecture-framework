package com.klynaf.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.klynaf.uicore.R

data class BottomNavItem(
    val screen: Screen,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.string.nav_label_home, Icons.Default.Home),
    BottomNavItem(Screen.Search, R.string.nav_label_search, Icons.Default.Search),
    BottomNavItem(Screen.Watchlist, R.string.nav_label_watchlist, Icons.Default.Star),
)
