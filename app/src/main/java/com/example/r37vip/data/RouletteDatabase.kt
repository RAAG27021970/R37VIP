package com.example.r37vip.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RouletteNumber::class, DelayStats::class], 
    version = 3,
    exportSchema = false
)
abstract class RouletteDatabase : RoomDatabase() {
    abstract fun rouletteNumberDao(): RouletteNumberDao

    companion object {
        @Volatile
        private var INSTANCE: RouletteDatabase? = null

        // Migración de versión 1 a 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `delay_stats` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `position` INTEGER NOT NULL,
                        `number` INTEGER NOT NULL,
                        `delay` INTEGER NOT NULL,
                        `type` TEXT NOT NULL DEFAULT 'street'
                    )
                """)
            }
        }

        // Migración de versión 2 a 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Recrear la tabla con el esquema actualizado
                database.execSQL("DROP TABLE IF EXISTS `delay_stats`")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `delay_stats` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `position` INTEGER NOT NULL,
                        `number` INTEGER NOT NULL,
                        `delay` INTEGER NOT NULL,
                        `type` TEXT NOT NULL
                    )
                """)
            }
        }

        fun getDatabase(context: Context): RouletteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RouletteDatabase::class.java,
                    "roulette_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)  // Agregamos las migraciones
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 