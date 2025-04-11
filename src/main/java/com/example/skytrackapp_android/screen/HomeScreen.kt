package com.example.skytrackapp_android.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skytrackapp_android.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(){
    val viewModel: WeatherViewModel = hiltViewModel()

    Column() {

    }
}