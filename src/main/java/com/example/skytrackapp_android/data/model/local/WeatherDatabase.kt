package com.example.skytrackapp_android.data.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skytrackapp_android.data.model.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
