package com.r37vip.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.r37vip.app.data.RouletteDatabase
import com.r37vip.app.data.RouletteNumber
import com.r37vip.app.data.DelayStats
import com.r37vip.app.data.RouletteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RouletteViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: RouletteRepository
    val allNumbers: Flow<List<RouletteNumber>>
    val allDelayStats: Flow<List<DelayStats>>

    init {
        val dao = RouletteDatabase.getDatabase(application).rouletteNumberDao()
        repository = RouletteRepository(dao)
        allNumbers = repository.allNumbers
        allDelayStats = repository.allDelayStats
    }

    fun insert(number: Int) {
        viewModelScope.launch {
            repository.insert(number)
        }
    }

    fun getLastNumbers(limit: Int): Flow<List<RouletteNumber>> {
        return repository.getLastNumbers(limit)
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun deleteLastNumber() {
        viewModelScope.launch {
            repository.deleteLastNumber()
        }
    }

    fun updateDelayStats(delayStats: List<DelayStats>) {
        viewModelScope.launch {
            repository.updateDelayStats(delayStats)
        }
    }

    fun resetCalculators() {
        viewModelScope.launch {
            // Eliminar todos los números
            repository.deleteAll()
            // Limpiar las estadísticas de retrasos
            repository.updateDelayStats(emptyList())
        }
    }
} 