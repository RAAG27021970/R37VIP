package com.example.r37vip.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.r37vip.data.RouletteDatabase
import com.example.r37vip.data.RouletteNumber
import com.example.r37vip.data.DelayStats
import com.example.r37vip.data.RouletteRepository
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