package com.example.skytrackapp_android.data.model.remote.fiveDayForecast

data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherData>,
    val city: City
)

