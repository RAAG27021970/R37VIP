package com.example.r37vip.data

import kotlinx.coroutines.flow.Flow

class RouletteRepository(private val rouletteNumberDao: RouletteNumberDao) {
    
    val allNumbers: Flow<List<RouletteNumber>> = rouletteNumberDao.getAllNumbers()
    
    suspend fun insert(number: Int) {
        rouletteNumberDao.insert(RouletteNumber(number = number))
    }
    
    fun getLastNumbers(limit: Int): Flow<List<RouletteNumber>> {
        return rouletteNumberDao.getLastNumbers(limit)
    }
    
    suspend fun deleteAll() {
        rouletteNumberDao.deleteAll()
    }

    suspend fun deleteLastNumber() {
        rouletteNumberDao.deleteLastNumber()
    }
} 