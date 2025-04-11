package com.example.skytrackapp_android.data.api

import com.example.skytrackapp_android.data.model.fiveDayForecast.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/forecast")
    suspend fun getFiveDayForecast(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): WeatherResponse
}