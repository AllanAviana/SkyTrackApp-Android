package com.example.skytrackapp_android.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytrackapp_android.R
import com.example.skytrackapp_android.data.model.DayWeather
import com.example.skytrackapp_android.data.model.HomeUiState
import com.example.skytrackapp_android.data.model.SearchUiState
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.CurrentWeatherResponse
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.WeatherData
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.WeatherResponse
import com.example.skytrackapp_android.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
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
        loadSavedWeathers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeather(
        city: String,
        updateHomeUiState: Boolean = true,
        apiKey: String = "91f5777c1db5ca6fa1a2a4002433a39f"
    ) {
        viewModelScope.launch {
            try {
                val result = repository.getFiveDayForecast(city, apiKey)
                val response = repository.getCurrentWeather(city, apiKey)

                if (result.list.isNotEmpty()) {
                    if (updateHomeUiState) {
                        _homeUiState.value = _homeUiState.value.copy(isLoading = true)
                        _weatherResponse.value = result
                        _currentWeatherResponse.value = response
                        updateHomeUiState()
                        _homeUiState.value = _homeUiState.value.copy(isLoading = false, isSuccessful = true)
                    }

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
            "clouds", "mist" -> if (isNight) R.drawable.clearnight else R.drawable.cloudy
            "rain", "drizzle" -> if (isNight) R.drawable.rainynight else R.drawable.rainy
            "snow" -> if (isNight) R.drawable.snowynight else R.drawable.snowy
            else -> if (isNight) R.drawable.clearnight else R.drawable.cloudy
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateHomeUiState() {
        _currentWeatherResponse.value?.let { current ->
            val weatherMain = current.weather[0].main
            val isNight = current.sys.pod == "n"
            val temperature = current.main.temp
            val maxTemperature = current.main.temp_max
            val minTemperature = current.main.temp_min

            _homeUiState.value = _homeUiState.value.copy(
                temperature = temperature.toInt(),
                maxTemperature = maxTemperature.toInt(),
                minTemperature = minTemperature.toInt(),
            )
        }

        _weatherResponse.value?.let { forecast ->
            val temps = forecast.list.take(8)
            val isNight = temps[0].sys.pod == "n"

            val dailyForecast = generateDailyForecast(forecast.list)

            _homeUiState.value = _homeUiState.value.copy(
                weatherResponse = forecast,
                temps = temps,
                image = getWeatherImage(temps[0].weather[0].main, isNight),
                dayWeather = dailyForecast
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateDailyForecast(data: List<WeatherData>): List<DayWeather> {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        val outputFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.US)

        return data.groupBy {
            LocalDate.parse(it.dt_txt, inputFormatter).toString()
        }.map { (date, list) ->
            val temps = list.map { it.main.temp }
            val max = list.maxOf { it.main.temp_max }
            val min = list.minOf { it.main.temp_min }

            val iconCode = list.groupBy { it.weather.first().icon }
                .maxByOrNull { it.value.size }?.key ?: "01d"

            DayWeather(
                day = LocalDate.parse(date).format(outputFormatter),
                temperature = temps.average().roundToInt().toString(),
                maxTemperature = max.roundToInt().toString(),
                minTemperature = min.roundToInt().toString(),
                weatherIconCode = iconCode
            )

        }
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
            Log.d("WeatherViewModel", "${ repository.getSavedWeathers() }")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSavedWeathers() {
        viewModelScope.launch {
            val saved = repository.getSavedWeathers()

            saved.forEachIndexed { index, cityEntry ->
                val isLast = index == saved.lastIndex
                fetchWeather(cityEntry.city, updateHomeUiState = isLast)
            }

            Log.d("WeatherViewModel", "saved: $saved")
        }
    }
}
