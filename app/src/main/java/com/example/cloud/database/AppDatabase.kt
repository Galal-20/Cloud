package com.example.cloud.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloud.database.dao.AlarmDao
import com.example.cloud.database.dao.WeatherDao
import com.example.cloud.database.entity.AlarmEntity
import com.example.cloud.database.entity.CurrentWeatherEntity

@Database(entities = [CurrentWeatherEntity::class, AlarmEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun alarmDao(): AlarmDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


