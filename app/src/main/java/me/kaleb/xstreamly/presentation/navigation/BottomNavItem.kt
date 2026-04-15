package me.kaleb.xstreamly.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import me.kaleb.xstreamly.R

data class BottomNavItem(
    val destination: AppDestination,
    val labelRes: Int,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        destination = AppDestination.Home,
        labelRes = R.string.title_home,
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        destination = AppDestination.LocalMedia,
        labelRes = R.string.title_local_media,
        icon = Icons.Filled.OndemandVideo
    ),
    BottomNavItem(
        destination = AppDestination.GoLive,
        labelRes = R.string.title_go_live,
        icon = Icons.Filled.LiveTv
    ),
    BottomNavItem(
        destination = AppDestination.Premiere,
        labelRes = R.string.title_premiere,
        icon = Icons.Filled.Schedule
    )
)

