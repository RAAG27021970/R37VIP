package com.example.r37vip.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouletteNumberDao {
    @Query("SELECT * FROM roulette_numbers ORDER BY timestamp ASC")
    fun getAllNumbers(): Flow<List<RouletteNumber>>

    @Query("SELECT * FROM roulette_numbers ORDER BY timestamp DESC LIMIT :limit")
    fun getLastNumbers(limit: Int): Flow<List<RouletteNumber>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(number: RouletteNumber)

    @Query("DELETE FROM roulette_numbers")
    suspend fun deleteAll()

    @Query("DELETE FROM roulette_numbers WHERE id = (SELECT id FROM roulette_numbers ORDER BY timestamp DESC LIMIT 1)")
    suspend fun deleteLastNumber()
} 