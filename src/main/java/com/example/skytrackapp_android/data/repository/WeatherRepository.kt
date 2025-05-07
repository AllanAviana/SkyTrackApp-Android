package com.example.skytrackapp_android.data.repository

import com.example.skytrackapp_android.data.api.WeatherApi
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.CurrentWeatherResponse
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi
) {
    suspend fun getFiveDayForecast(city: String, apiKey: String): WeatherResponse {
        return api.getFiveDayForecast(city, apiKey)
    }

    suspend fun getCurrentWeather(city: String, apiKey: String): CurrentWeatherResponse {
        return api.getCurrentWeather(city, apiKey)
    }
}
