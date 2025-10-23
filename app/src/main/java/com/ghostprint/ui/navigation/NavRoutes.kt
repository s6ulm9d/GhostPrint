package com.ghostprint.ui.navigation

sealed class NavRoutes(val route: String) {
    object Controls : NavRoutes("controls")
    object Logs : NavRoutes("logs")
}