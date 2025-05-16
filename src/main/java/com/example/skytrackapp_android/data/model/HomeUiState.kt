package com.example.skytrackapp_android.data.model

import com.example.skytrackapp_android.R
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.WeatherData
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.WeatherResponse

data class HomeUiState(
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val weatherResponse: WeatherResponse? = null,
    val image: Int = R.drawable.clearnight,
    val temperature: Int = 0,
    val maxTemperature: Int = 0,
    val minTemperature: Int = 0,
    val temps: List<WeatherData> = emptyList(),
    val dayWeather: List<DayWeather> = emptyList()
)
