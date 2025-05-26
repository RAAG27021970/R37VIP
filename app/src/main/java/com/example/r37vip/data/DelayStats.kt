package com.example.r37vip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delay_stats")
data class DelayStats(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val position: Int,  // Posición del atraso (0-3)
    val number: Int,    // Número almacenado en esa posición (-1 si no hay número)
    val delay: Int,      // Valor del atraso
    val type: String = "street" // Puede ser "street" o "series"
) 