package com.klynaf

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.klynaf.feature.detail.presentation.DetailScreen
import com.klynaf.feature.home.presentation.HomeScreen
import com.klynaf.feature.search.presentation.SearchScreen
import com.klynaf.feature.watchlist.presentation.WatchlistScreen
import com.klynaf.navigation.Screen
import com.klynaf.navigation.bottomNavItems
import com.klynaf.uicore.theme.KlynAFTheme

@Composable
fun KlynAFAppContent() {
    KlynAFTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val isTopLevelDestination = bottomNavItems.any { it.screen.route == currentDestination?.route }
        Scaffold(
            bottomBar = {
                if (isTopLevelDestination) {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                                label = { Text(stringResource(item.labelRes)) },
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToDetail = { id, type ->
                            navController.navigate(Screen.Detail.createRoute(id, type))
                        }
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        onNavigateToDetail = { id, type ->
                            navController.navigate(Screen.Detail.createRoute(id, type))
                        }
                    )
                }
                composable(Screen.Watchlist.route) {
                    WatchlistScreen(
                        onNavigateToDetail = { id, type ->
                            navController.navigate(Screen.Detail.createRoute(id, type))
                        }
                    )
                }
                composable(
                    route = Screen.Detail.route,
                    arguments = listOf(
                        navArgument("mediaId") { type = NavType.IntType },
                        navArgument("mediaType") { type = NavType.StringType },
                    )
                ) {
                    DetailScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDetail = { id, type ->
                            navController.navigate(Screen.Detail.createRoute(id, type))
                        }
                    )
                }
            }
        }
    }
}
