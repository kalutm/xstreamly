package me.kaleb.xstreamly.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.kaleb.xstreamly.presentation.golive.GoLiveScreen
import me.kaleb.xstreamly.presentation.home.HomeScreen
import me.kaleb.xstreamly.presentation.localmedia.LocalMediaScreen
import me.kaleb.xstreamly.presentation.premiere.PremiereScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppDestination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = AppDestination.Home.route) {
            HomeScreen()
        }
        composable(route = AppDestination.LocalMedia.route) {
            LocalMediaScreen()
        }
        composable(route = AppDestination.GoLive.route) {
            GoLiveScreen()
        }
        composable(route = AppDestination.Premiere.route) {
            PremiereScreen()
        }
    }
}

