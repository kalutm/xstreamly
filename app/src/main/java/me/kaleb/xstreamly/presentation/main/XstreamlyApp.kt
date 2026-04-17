package me.kaleb.xstreamly.presentation.main

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.kaleb.xstreamly.R
import me.kaleb.xstreamly.presentation.navigation.AppDestination
import me.kaleb.xstreamly.presentation.navigation.AppNavGraph
import me.kaleb.xstreamly.presentation.navigation.bottomNavItems
import me.kaleb.xstreamly.ui.theme.XstreamlyTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun XstreamlyApp() {
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    XstreamlyTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = routeTitle(currentRoute))
                    },
                    actions = {
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { isDarkTheme = it }
                        )
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.destination.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(imageVector = item.icon, contentDescription = stringResource(item.labelRes))
                            },
                            label = {
                                Text(text = stringResource(item.labelRes))
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun routeTitle(route: String?): String {
    return when (route) {
        AppDestination.Home.route -> stringResource(R.string.title_home)
        AppDestination.LocalMedia.route -> stringResource(R.string.title_local_media)
        AppDestination.GoLive.route -> stringResource(R.string.title_go_live)
        AppDestination.Premiere.route -> stringResource(R.string.title_premiere)
        else -> stringResource(R.string.app_name)
    }
}

