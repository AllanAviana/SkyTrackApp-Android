package com.example.skytrackapp_android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytrackapp_android.R
import com.example.skytrackapp_android.data.model.HomeUiState
import com.example.skytrackapp_android.data.model.SearchUiState
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.CurrentWeatherResponse
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.WeatherResponse
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

    private val _currentWeatherResponse = MutableStateFlow<CurrentWeatherResponse?>(null)

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    init {
        fetchWeather("Sao Paulo")
        loadSavedWeathers()
    }

    fun fetchWeather(city: String, apiKey: String = "91f5777c1db5ca6fa1a2a4002433a39f") {
        viewModelScope.launch {
            try {
                val result = repository.getFiveDayForecast(city, apiKey)
                val response = repository.getCurrentWeather(city, apiKey)

                if (result.list.isNotEmpty()) {
                    _homeUiState.value = _homeUiState.value.copy(isLoading = true)
                    _weatherResponse.value = result
                    _currentWeatherResponse.value = response
                    updateHomeUiState()
                    _homeUiState.value = _homeUiState.value.copy(isLoading = false, isSuccessful = true)

                    val cityName = response.name
                    val temp = response.main.temp.toInt().toString()

                    val newEntry = mapOf("city" to cityName, "temperature" to temp)

                    _searchUiState.value = _searchUiState.value.copy(success = true)

                    val alreadyExists = _searchUiState.value.weathers.any {
                        it["city"]?.equals(cityName, ignoreCase = true) == true
                    }

                    if (!alreadyExists) {
                        _searchUiState.value = _searchUiState.value.copy(
                            weathers = _searchUiState.value.weathers + newEntry
                        )
                        saveWeather(cityName, temp)
                    }

                } else {
                    _searchUiState.value = _searchUiState.value.copy(error = true)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("WeatherViewModel", "Exception occurred: ${e.message}", e)
                _searchUiState.value = _searchUiState.value.copy(error = true)
            }
        }
    }

    private fun getWeatherImage(main: String, isNight: Boolean): Int {
        return when (main.lowercase()) {
            "clear" -> if (isNight) R.drawable.clearnight else R.drawable.clearsky
            "clouds", "Mist" -> if (isNight) R.drawable.clearnight else R.drawable.cloudy
            "rain", "drizzle" -> if (isNight) R.drawable.rainynight else R.drawable.rainy
            "snow" -> if (isNight) R.drawable.snowynight else R.drawable.snowy
            else ->if (isNight) R.drawable.clearnight else R.drawable.cloudy
        }
    }

    fun updateHomeUiState() {
        _currentWeatherResponse.value?.let { current ->
            val weatherMain = current.weather[0].main
            val isNight = current.sys.pod == "n"
            val temperature = current.main.temp
            val maxTemperature = current.main.temp_max
            val minTemperature = current.main.temp_min
            Log.d("WeatherViewModel", "response: $current")
            Log.d("WeatherViewModel", "weatherMain: $weatherMain")
            Log.d("WeatherViewModel", "isNight: $isNight")
            Log.d("WeatherViewModel", "temperature: $temperature")

            _homeUiState.value = _homeUiState.value.copy(
                temperature = temperature.toInt(),
                maxTemperature = maxTemperature.toInt(),
                minTemperature = minTemperature.toInt(),
            )
        }

        _weatherResponse.value?.let { forecast ->
            val temps = forecast.list.take(8)
            val isNight = temps[0].sys.pod == "n"
            _homeUiState.value = _homeUiState.value.copy(
                weatherResponse = forecast,
                temps = temps,
                image = getWeatherImage(temps[0].weather[0].main, isNight)
            )
        }

        Log.d("WeatherViewModel", "temp: ${_homeUiState.value.temperature}")
    }

    fun updateCity(city: String) {
        _searchUiState.value = _searchUiState.value.copy(city = city)
    }

    fun resetSearchUiState() {
        _searchUiState.value = _searchUiState.value.copy(
            city = "",
            success = false,
            error = false
        )
    }

    fun saveWeather(city: String, temp: String) {
        viewModelScope.launch {
            val existing = repository.getSavedWeathers()
            if (existing.none { it.city.equals(city, ignoreCase = true) }) {
                repository.saveWeather(city, temp)
            }
        }
    }


    fun loadSavedWeathers() {
        viewModelScope.launch {
            val saved = repository.getSavedWeathers()
            val mapped = saved.map { mapOf("city" to it.city, "temperature" to it.temperature) }
            _searchUiState.value = _searchUiState.value.copy(weathers = mapped)
        }
    }
}