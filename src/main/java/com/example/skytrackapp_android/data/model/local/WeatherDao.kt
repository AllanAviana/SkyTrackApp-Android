package com.example.skytrackapp_android.data.model.local

import androidx.room.*
import com.example.skytrackapp_android.data.model.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather_table")
    suspend fun getAllWeathers(): List<WeatherEntity>

    @Query("DELETE FROM weather_table")
    suspend fun clearAll()
}
