package com.example.skytrackapp_android.presentation.navigation

sealed class Routes(val route: String) {
    object HomeScreen : Routes("home")
    object SearchScreen : Routes("search")
}