package com.example.r37vip.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RouletteNumber::class], version = 1)
abstract class RouletteDatabase : RoomDatabase() {
    abstract fun rouletteNumberDao(): RouletteNumberDao

    companion object {
        @Volatile
        private var INSTANCE: RouletteDatabase? = null

        fun getDatabase(context: Context): RouletteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RouletteDatabase::class.java,
                    "roulette_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 