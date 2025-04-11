package com.example.skytrackapp_android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytrackapp_android.data.model.fiveDayForecast.WeatherResponse
import com.example.skytrackapp_android.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherResponse = MutableStateFlow<WeatherResponse?>(null)
    val weatherResponse: StateFlow<WeatherResponse?> = _weatherResponse.asStateFlow()

    init {
        fetchWeather("London")
    }

    fun fetchWeather(city: String, apiKey: String = "91f5777c1db5ca6fa1a2a4002433a39f") {
        viewModelScope.launch {
            try {
                val result = repository.getFiveDayForecast(city, apiKey)
                _weatherResponse.value = result
                 Log.d("WeatherViewModel", "_weatherResponse: $result")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("WeatherViewModel", "_weatherResponse:")
                _weatherResponse.value = null
            }
        }
    }
}