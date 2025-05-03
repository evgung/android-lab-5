package com.example.laba5.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.laba5.data.dao.FlightDao
import com.example.laba5.data.entities.Airport
import com.example.laba5.data.entities.Favourite

@Database(
    entities = [Airport::class, Favourite::class],
    version = 1,
    exportSchema = false
)
abstract class FlightsDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile
        private var instance: FlightsDatabase? = null

        fun getInstance(context: Context): FlightsDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightsDatabase::class.java,
                    "flight_search.db"
                ).createFromAsset("database/flight_search.db")
                    .build()

                instance = newInstance
                newInstance
            }
        }
    }
}