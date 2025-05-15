package com.example.skytrackapp_android.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import com.example.skytrackapp_android.presentation.screen.HomeScreen
import com.example.skytrackapp_android.presentation.screen.SearchScreen
import com.example.skytrackapp_android.viewmodel.WeatherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = hiltViewModel()

    AnimatedNavHost(
        navController = navController,
        startDestination = Routes.HomeScreen.route,
        enterTransition = { slideInHorizontally { it } },      // direita para esquerda
        exitTransition = { slideOutHorizontally { -it } },      // esquerda para esquerda
        popEnterTransition = { slideInHorizontally { -it } },   // esquerda para direita
        popExitTransition = { slideOutHorizontally { it } }     // direita para direita
    ) {
        composable(Routes.HomeScreen.route) {
            HomeScreen(viewModel, navController)
        }
        composable(Routes.SearchScreen.route) {
            SearchScreen(viewModel, navController)
        }
    }
}
