package com.example.r37vip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roulette_numbers")
data class RouletteNumber(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: Int,
    val timestamp: Long = System.currentTimeMillis()
) 