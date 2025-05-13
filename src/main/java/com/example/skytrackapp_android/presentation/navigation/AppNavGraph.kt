package com.example.skytrackapp_android.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skytrackapp_android.presentation.screen.HomeScreen
import com.example.skytrackapp_android.presentation.screen.SearchScreen
import com.example.skytrackapp_android.viewmodel.WeatherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(){
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = Routes.HomeScreen.route) {
        composable(Routes.HomeScreen.route) {
            HomeScreen(viewModel, navController)
        }
        composable(Routes.SearchScreen.route) {
            SearchScreen(viewModel, navController)
        }
    }
}