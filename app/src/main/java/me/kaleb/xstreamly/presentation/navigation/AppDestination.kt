package me.kaleb.xstreamly.presentation.navigation

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object LocalMedia : AppDestination("local_media")
    data object GoLive : AppDestination("go_live")
    data object Premiere : AppDestination("premiere")
}