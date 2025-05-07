package com.example.skytrackapp_android.data.model.remote.fiveDayForecast

data class WeatherData(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain? = null,
    val sys: Sys,
    val dt_txt: String
)

