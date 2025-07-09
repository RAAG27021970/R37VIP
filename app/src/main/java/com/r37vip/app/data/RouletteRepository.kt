package com.r37vip.app.data

import kotlinx.coroutines.flow.Flow

class RouletteRepository(private val rouletteNumberDao: RouletteNumberDao) {
    
    val allNumbers: Flow<List<RouletteNumber>> = rouletteNumberDao.getAllNumbers()
    val allDelayStats: Flow<List<DelayStats>> = rouletteNumberDao.getAllDelayStats()
    
    suspend fun insert(number: Int) {
        rouletteNumberDao.insert(RouletteNumber(number = number))
    }
    
    fun getLastNumbers(limit: Int): Flow<List<RouletteNumber>> {
        return rouletteNumberDao.getLastNumbers(limit)
    }
    
    suspend fun deleteAll() {
        rouletteNumberDao.deleteAll()
        rouletteNumberDao.deleteAllDelayStats()
    }

    suspend fun deleteLastNumber() {
        rouletteNumberDao.deleteLastNumber()
    }

    suspend fun updateDelayStats(delayStats: List<DelayStats>) {
        rouletteNumberDao.updateAllDelayStats(delayStats)
    }
} 