package com.r37vip.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.r37vip.app.data.RouletteNumber
import com.r37vip.app.data.DelayStats
import com.r37vip.app.stats.StreetDelayCalculator
import com.r37vip.app.stats.SeriesDelayCalculator
import com.r37vip.app.viewmodels.RouletteViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InputFragment : Fragment() {
    
    // Variable configurable para el objetivo de fichas a ganar
    // Cambiar este valor para ajustar la agresividad de la progresión
    private val TARGET_FICHAS_OBJETIVO = 5
    
    // Clase de datos para almacenar información de apuestas
    data class BetInfo(
        val columnCount: Int,        // Cantidad de números distintos en la columna
        val betAmount: Int,          // Apuesta calculada en fichas
        val totalBetAccumulated: Int // Acumulado total de apuestas hasta el momento
    )
    
    private lateinit var numberInput: EditText
    private lateinit var numericKeypad: GridLayout
    private lateinit var stat1: TextView
    private lateinit var stat2: TextView
    private lateinit var stat3: TextView
    private lateinit var stat4: TextView
    private lateinit var stat5: TextView
    private lateinit var stat6: TextView
    private lateinit var stat7: TextView
    private lateinit var stat8: TextView
    
    // Nuevos indicadores para los últimos números
    private lateinit var lastNumber1: TextView
    private lateinit var lastNumber2: TextView
    private lateinit var lastNumber3: TextView
    private lateinit var lastNumber4: TextView
    private lateinit var lastNumber5: TextView
    private lateinit var lastNumber6: TextView
    private lateinit var lastNumber7: TextView
    private lateinit var lastNumber8: TextView
    private lateinit var lastNumber9: TextView
    private lateinit var lastNumber10: TextView

    // Contadores
    private lateinit var counter1: TextView
    private lateinit var counter2: TextView
    private lateinit var counter3: TextView
    private lateinit var counter4: TextView

    // Contadores de columnas
    private lateinit var column1Counter: TextView
    private lateinit var column2Counter: TextView
    private lateinit var column3Counter: TextView

    // Contadores secundarios de columnas
    private lateinit var column1Counter2: TextView
    private lateinit var column2Counter2: TextView
    private lateinit var column3Counter2: TextView

    // Fichas por número de apuesta
    private lateinit var betFichasCol1: TextView
    private lateinit var betFichasCol2: TextView
    private lateinit var betFichasCol3: TextView

    // Acumulados de fichas gastadas por columna
    private lateinit var accumulatedBetFichasCol1: TextView
    private lateinit var accumulatedBetFichasCol2: TextView
    private lateinit var accumulatedBetFichasCol3: TextView

    // Progresiones
    private lateinit var progresion1: TextView
    private lateinit var progresion2: TextView
    private lateinit var progresion3: TextView
    private lateinit var progresion4: TextView

    // Arrays para apuestas de columnas con información completa
    private val betCOL1 = mutableListOf<BetInfo>()
    private val betCOL2 = mutableListOf<BetInfo>()
    private val betCOL3 = mutableListOf<BetInfo>()

    private val viewModel: RouletteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            Log.d(TAG, "Iniciando onViewCreated")
            
            try {
                numberInput = view.findViewById(R.id.numberInput)
                numericKeypad = view.findViewById(R.id.numericKeypad)
                
                // Initialize first group of statistics
                stat1 = view.findViewById(R.id.stat1)
                stat2 = view.findViewById(R.id.stat2)
                stat3 = view.findViewById(R.id.stat3)
                stat4 = view.findViewById(R.id.stat4)
                
                // Initialize second group of statistics
                stat5 = view.findViewById(R.id.stat5)
                stat6 = view.findViewById(R.id.stat6)
                stat7 = view.findViewById(R.id.stat7)
                stat8 = view.findViewById(R.id.stat8)
                
                // Initialize last numbers indicators
                lastNumber1 = view.findViewById(R.id.lastNumber1)
                lastNumber2 = view.findViewById(R.id.lastNumber2)
                lastNumber3 = view.findViewById(R.id.lastNumber3)
                lastNumber4 = view.findViewById(R.id.lastNumber4)
                lastNumber5 = view.findViewById(R.id.lastNumber5)
                lastNumber6 = view.findViewById(R.id.lastNumber6)
                lastNumber7 = view.findViewById(R.id.lastNumber7)
                lastNumber8 = view.findViewById(R.id.lastNumber8)
                lastNumber9 = view.findViewById(R.id.lastNumber9)
                lastNumber10 = view.findViewById(R.id.lastNumber10)

                // Initialize counters
                counter1 = view.findViewById(R.id.counter1)
                counter2 = view.findViewById(R.id.counter2)
                counter3 = view.findViewById(R.id.counter3)
                counter4 = view.findViewById(R.id.counter4)

                // Initialize column counters
                column1Counter = view.findViewById(R.id.column1Counter)
                column2Counter = view.findViewById(R.id.column2Counter)
                column3Counter = view.findViewById(R.id.column3Counter)

                // Initialize secondary column counters
                column1Counter2 = view.findViewById(R.id.column1Counter2)
                column2Counter2 = view.findViewById(R.id.column2Counter2)
                column3Counter2 = view.findViewById(R.id.column3Counter2)

                // Initialize bet fichas per number
                betFichasCol1 = view.findViewById(R.id.betFichasCol1)
                betFichasCol2 = view.findViewById(R.id.betFichasCol2)
                betFichasCol3 = view.findViewById(R.id.betFichasCol3)

                // Initialize accumulated bet fichas per column
                accumulatedBetFichasCol1 = view.findViewById(R.id.accumulatedBetFichasCol1)
                accumulatedBetFichasCol2 = view.findViewById(R.id.accumulatedBetFichasCol2)
                accumulatedBetFichasCol3 = view.findViewById(R.id.accumulatedBetFichasCol3)

                // Initialize progresions
                progresion1 = view.findViewById(R.id.progresion1)
                progresion2 = view.findViewById(R.id.progresion2)
                progresion3 = view.findViewById(R.id.progresion3)
                progresion4 = view.findViewById(R.id.progresion4)
                
                // Initialize column counters to zero
                column1Counter.text = "0"
                column2Counter.text = "0"
                column3Counter.text = "0"
                updateCounterColor(column1Counter, 0)
                updateCounterColor(column2Counter, 0)
                updateCounterColor(column3Counter, 0)
                
                // Initialize secondary column counters to zero
                column1Counter2.text = "0"
                column2Counter2.text = "0"
                column3Counter2.text = "0"
                updateCounterColor(column1Counter2, 0)
                updateCounterColor(column2Counter2, 0)
                updateCounterColor(column3Counter2, 0)
                
                // Initialize bet fichas per number to zero
                betFichasCol1.text = "0"
                betFichasCol2.text = "0"
                betFichasCol3.text = "0"
                updateBetFichasColor(betFichasCol1, 0)
                updateBetFichasColor(betFichasCol2, 0)
                updateBetFichasColor(betFichasCol3, 0)
                
                // Initialize accumulated bet fichas to zero
                accumulatedBetFichasCol1.text = "0"
                accumulatedBetFichasCol2.text = "0"
                accumulatedBetFichasCol3.text = "0"
                
                // Reset bet arrays
                betCOL1.clear()
                betCOL2.clear()
                betCOL3.clear()
                
                Log.d(TAG, "Views inicializadas correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error inicializando views", e)
                return
            }

            try {
                setupNumericKeypad()
                Log.d(TAG, "Teclado numérico configurado")
            } catch (e: Exception) {
                Log.e(TAG, "Error configurando teclado", e)
            }

            try {
                loadSavedIndicators()
                Log.d(TAG, "Indicadores cargados")
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando indicadores", e)
            }

            try {
                observeDelayStats()
                Log.d(TAG, "Observación de stats iniciada")
            } catch (e: Exception) {
                Log.e(TAG, "Error iniciando observación de stats", e)
            }

            try {
                observeNumbers()
                Log.d(TAG, "Observación de números iniciada")
            } catch (e: Exception) {
                Log.e(TAG, "Error iniciando observación de números", e)
            }
            
            Log.d(TAG, "onViewCreated completado")
        } catch (e: Exception) {
            Log.e(TAG, "Error general en onViewCreated", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearOldCounterPreferences()
    }

    private fun clearOldCounterPreferences() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        
        // Eliminar las preferencias de los contadores antiguos
        for (i in 1..8) {
            editor.remove("counter$i")
        }
        for (i in 1..4) {
            editor.remove("totalCnt$i")
        }
        
        editor.apply()
        Log.d(TAG, "Preferencias antiguas de contadores eliminadas")
    }

    private fun observeNumbers() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(TAG, "Iniciando observación de números")
                viewModel.allNumbers.collectLatest { numbers ->
                    try {
                        Log.d(TAG, "Recibida actualización de números: ${numbers.size} números")
                        // Solo actualizamos las estadísticas si hay un nuevo número
                        if (numbers.isEmpty()) {
                            Log.d(TAG, "Lista vacía, reseteando indicadores")
                            resetAllIndicators()
                        } else {
                            val lastNumber = numbers.last()
                            Log.d(TAG, "Último número: ${lastNumber.number}, ID: ${lastNumber.id}")
                            if (lastNumber.id > 0) {
                                updateStatistics(numbers)
                                updateLastNumbers(numbers)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error procesando números", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en observeNumbers", e)
            }
        }
    }

    private fun setNumberColor(textView: TextView, number: Int) {
        when (number) {
            0 -> {
                textView.setBackgroundColor(android.graphics.Color.BLACK)
                textView.setTextColor(android.graphics.Color.WHITE)
            }
            1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34 -> {
                textView.setBackgroundColor(android.graphics.Color.rgb(0, 200, 83)) // Verde
                textView.setTextColor(android.graphics.Color.WHITE)
            }
            2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35 -> {
                textView.setBackgroundColor(android.graphics.Color.YELLOW) // Amarillo
                textView.setTextColor(android.graphics.Color.BLACK)
            }
            3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36 -> {
                textView.setBackgroundColor(android.graphics.Color.RED)
                textView.setTextColor(android.graphics.Color.WHITE)
            }
            else -> {
                textView.setBackgroundColor(android.graphics.Color.WHITE)
                textView.setTextColor(android.graphics.Color.BLACK)
            }
        }
    }

    private fun updateLastNumbers(numbers: List<RouletteNumber>) {
        try {
            // Resetear los últimos números y sus colores
            val lastNumberViews = listOf(
                lastNumber1, lastNumber2, lastNumber3, lastNumber4, lastNumber5,
                lastNumber6, lastNumber7, lastNumber8, lastNumber9, lastNumber10
            )
            lastNumberViews.forEach {
                it.text = "-"
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }
            
            // Para la visualización, usamos los últimos 10 números
            val lastTenForDisplay = numbers.takeLast(10)
            lastTenForDisplay.forEachIndexed { index, rouletteNumber ->
                if (index < lastNumberViews.size) {
                    lastNumberViews[index].text = rouletteNumber.number.toString()
                    setNumberColor(lastNumberViews[index], rouletteNumber.number)
                }
            }

            // Para la lógica de los contadores, usamos los últimos 11 números
            // (el último número se compara con los 10 anteriores)
            val lastElevenForLogic = numbers.takeLast(11)
            updateColumnCounters(lastElevenForLogic.map { it.number })
            
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando últimos números", e)
        }
    }

    private fun updateColumnCounters(numbers: List<Int>) {
        val newNumber = numbers.lastOrNull() ?: return // Salir si no hay números

        // --- Lógica para Columna 1 ---
        if (isInColumn1(newNumber)) {
            val colNumbers = numbers.filter { isInColumn1(it) }
            val isRepetition = colNumbers.dropLast(1).contains(newNumber)
            
            if (isRepetition) {
                // Poner contador principal en 0
                column1Counter.text = "0"
                updateCounterColor(column1Counter, 0)
                
                // Incrementar contador de eventos
                val prevEventCount = column1Counter2.text.toString().toIntOrNull() ?: 0
                val newEventCount = prevEventCount + 1
                column1Counter2.text = newEventCount.toString()
                updateCounterColor(column1Counter2, newEventCount)
                
                // Reiniciar array de apuestas COL1
                betCOL1.clear()
                betFichasCol1.text = "0"
                updateBetFichasColor(betFichasCol1, 0)
            } else {
                // Incrementar contador principal
                val prevCount = column1Counter.text.toString().toIntOrNull() ?: 0
                val newCount = prevCount + 1
                column1Counter.text = newCount.toString()
                updateCounterColor(column1Counter, newCount)
            }
        }

        // --- Lógica para Columna 2 ---
        if (isInColumn2(newNumber)) {
            val colNumbers = numbers.filter { isInColumn2(it) }
            val isRepetition = colNumbers.dropLast(1).contains(newNumber)

            if (isRepetition) {
                // Poner contador principal en 0
                column2Counter.text = "0"
                updateCounterColor(column2Counter, 0)

                // Incrementar contador de eventos
                val prevEventCount = column2Counter2.text.toString().toIntOrNull() ?: 0
                val newEventCount = prevEventCount + 1
                column2Counter2.text = newEventCount.toString()
                updateCounterColor(column2Counter2, newEventCount)
                
                // Reiniciar array de apuestas COL2
                betCOL2.clear()
                betFichasCol2.text = "0"
                updateBetFichasColor(betFichasCol2, 0)
            } else {
                // Incrementar contador principal
                val prevCount = column2Counter.text.toString().toIntOrNull() ?: 0
                val newCount = prevCount + 1
                column2Counter.text = newCount.toString()
                updateCounterColor(column2Counter, newCount)
            }
        }

        // --- Lógica para Columna 3 ---
        if (isInColumn3(newNumber)) {
            val colNumbers = numbers.filter { isInColumn3(it) }
            val isRepetition = colNumbers.dropLast(1).contains(newNumber)

            if (isRepetition) {
                // Poner contador principal en 0
                column3Counter.text = "0"
                updateCounterColor(column3Counter, 0)

                // Incrementar contador de eventos
                val prevEventCount = column3Counter2.text.toString().toIntOrNull() ?: 0
                val newEventCount = prevEventCount + 1
                column3Counter2.text = newEventCount.toString()
                updateCounterColor(column3Counter2, newEventCount)
                
                // Reiniciar array de apuestas COL3
                betCOL3.clear()
                betFichasCol3.text = "0"
                updateBetFichasColor(betFichasCol3, 0)
            } else {
                // Incrementar contador principal
                val prevCount = column3Counter.text.toString().toIntOrNull() ?: 0
                val newCount = prevCount + 1
                column3Counter.text = newCount.toString()
                updateCounterColor(column3Counter, newCount)
            }
        }
        
        // Actualizar arrays de apuestas después de cambiar los contadores
        updateBetArrays(numbers)
    }

    private fun getColorForValue(value: Int, maxValue: Int = 8): Int {
        // Normalizar el valor entre 0 y 1, con máximo de 8
        val normalizedValue = (value.toFloat() / maxValue).coerceIn(0f, 1f)
        
        // Crear gama de colores: amarillo (1) -> naranja (4) -> rojo (8)
        val red: Int
        val green: Int
        val blue = 0
        
        when {
            normalizedValue <= 0.5f -> {
                // De amarillo a naranja (0.0 a 0.5)
                val factor = normalizedValue * 2 // 0 a 1
                red = (255 * factor).toInt()
                green = 255
            }
            else -> {
                // De naranja a rojo (0.5 a 1.0)
                val factor = (normalizedValue - 0.5f) * 2 // 0 a 1
                red = 255
                green = (255 * (1 - factor)).toInt()
            }
        }
        
        return android.graphics.Color.rgb(red, green, blue)
    }

    private fun updateCounterColor(textView: TextView, value: Int) {
        try {
            if (value == 0) {
                // Si el valor es 0, fondo negro y letra blanca
                textView.setBackgroundColor(android.graphics.Color.BLACK)
                textView.setTextColor(android.graphics.Color.WHITE)
            } else {
                val color = getColorForValue(value, 8)
                textView.setBackgroundColor(color)
                
                // Calcular el contraste del texto basado en el color de fondo
                val textColor = getContrastTextColor(color)
                textView.setTextColor(textColor)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando color del contador", e)
        }
    }

    private fun getContrastTextColor(backgroundColor: Int): Int {
        // Extraer los componentes RGB del color de fondo
        val red = android.graphics.Color.red(backgroundColor)
        val green = android.graphics.Color.green(backgroundColor)
        val blue = android.graphics.Color.blue(backgroundColor)
        
        // Calcular la luminancia del color de fondo
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
        
        // Si la luminancia es alta (fondo claro), usar texto negro
        // Si la luminancia es baja (fondo oscuro), usar texto blanco
        return if (luminance > 0.5) {
            android.graphics.Color.BLACK
        } else {
            android.graphics.Color.WHITE
        }
    }

    private fun isInColumn1(number: Int): Boolean {
        return number in listOf(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34)
    }

    private fun isInColumn2(number: Int): Boolean {
        return number in listOf(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35)
    }

    private fun isInColumn3(number: Int): Boolean {
        return number in listOf(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36)
    }

    private fun resetAllIndicators() {
        try {
            // Reset statistics
            stat1.text = getString(R.string.empty_stat)
            stat2.text = getString(R.string.empty_stat)
            stat3.text = getString(R.string.empty_stat)
            stat4.text = getString(R.string.empty_stat)
            stat5.text = getString(R.string.empty_stat)
            stat6.text = getString(R.string.empty_stat)
            stat7.text = getString(R.string.empty_stat)
            stat8.text = getString(R.string.empty_stat)

            // Reset statistics colors
            val statViews = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
            statViews.forEach {
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }

            // Reset last numbers
            lastNumber1.text = getString(R.string.empty_stat)
            lastNumber2.text = getString(R.string.empty_stat)
            lastNumber3.text = getString(R.string.empty_stat)
            lastNumber4.text = getString(R.string.empty_stat)
            lastNumber5.text = getString(R.string.empty_stat)
            lastNumber6.text = getString(R.string.empty_stat)
            lastNumber7.text = getString(R.string.empty_stat)
            lastNumber8.text = getString(R.string.empty_stat)
            lastNumber9.text = getString(R.string.empty_stat)
            lastNumber10.text = getString(R.string.empty_stat)

            // Reset last numbers colors
            val lastNumberViews = listOf(
                lastNumber1, lastNumber2, lastNumber3, lastNumber4, lastNumber5,
                lastNumber6, lastNumber7, lastNumber8, lastNumber9, lastNumber10
            )
            lastNumberViews.forEach {
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }

            // Reset counters
            counter1.text = "0"
            counter2.text = "0"
            counter3.text = "0"
            counter4.text = "0"

            // Reset counter colors using new color scheme
            updateCounterColor(counter1, 0)
            updateCounterColor(counter2, 0)
            updateCounterColor(counter3, 0)
            updateCounterColor(counter4, 0)

            // Reset column counters
            column1Counter.text = "0"
            column2Counter.text = "0"
            column3Counter.text = "0"
            updateCounterColor(column1Counter, 0)
            updateCounterColor(column2Counter, 0)
            updateCounterColor(column3Counter, 0)

            // Reset secondary column counters
            column1Counter2.text = "0"
            column2Counter2.text = "0"
            column3Counter2.text = "0"
            updateCounterColor(column1Counter2, 0)
            updateCounterColor(column2Counter2, 0)
            updateCounterColor(column3Counter2, 0)

            // Reset bet fichas per number
            betFichasCol1.text = "0"
            betFichasCol2.text = "0"
            betFichasCol3.text = "0"
            updateBetFichasColor(betFichasCol1, 0)
            updateBetFichasColor(betFichasCol2, 0)
            updateBetFichasColor(betFichasCol3, 0)

            // Reset progresions
            progresion1.text = "1"
            progresion2.text = "1"
            progresion3.text = "1"
            progresion4.text = "1"

            // Reset progresion colors using new color scheme
            updateProgresionColor(progresion1, 1)
            updateProgresionColor(progresion2, 1)
            updateProgresionColor(progresion3, 1)
            updateProgresionColor(progresion4, 1)

            // Reset accumulated bet fichas
            accumulatedBetFichasCol1.text = "0"
            accumulatedBetFichasCol2.text = "0"
            accumulatedBetFichasCol3.text = "0"
            
            // Reset bet arrays
            betCOL1.clear()
            betCOL2.clear()
            betCOL3.clear()
        } catch (e: Exception) {
            Log.e(TAG, "Error reseteando indicadores", e)
        }
    }

    private fun observeDelayStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(TAG, "Iniciando observación de DelayStats")
                viewModel.allDelayStats.collectLatest { delayStats ->
                    try {
                        if (delayStats.isNotEmpty()) {
                            Log.d(TAG, "Restaurando estado - DelayStats: $delayStats")
                            
                            // Separar stats por tipo
                            val streetStats = delayStats.filter { it.type == "street" }.sortedBy { it.position }
                            val seriesStats = delayStats.filter { it.type == "series" }.sortedBy { it.position }
                            
                            Log.d(TAG, "Stats de calles: $streetStats")
                            Log.d(TAG, "Stats de series: $seriesStats")
                            
                            try {
                                // Restaurar estado de calles
                                if (streetStats.isNotEmpty()) {
                                    // Asegurarnos de tener exactamente 4 elementos
                                    val streetNumbers = streetStats.map { it.number }.toMutableList()
                                    val streetDelays = streetStats.map { it.delay }.toMutableList()
                                    
                                    // Rellenar con -1 y 0 hasta tener 4 elementos
                                    while (streetNumbers.size < 4) {
                                        streetNumbers.add(-1)
                                        streetDelays.add(0)
                                    }
                                    // Tomar solo los primeros 4 elementos si hay más
                                    val finalStreetNumbers = streetNumbers.take(4).toIntArray()
                                    val finalStreetDelays = streetDelays.take(4).toIntArray()
                                    
                                    Log.d(TAG, "Restaurando estado de calles - Números: ${finalStreetNumbers.contentToString()}")
                                    Log.d(TAG, "Restaurando estado de calles - Atrasos: ${finalStreetDelays.contentToString()}")
                                    StreetDelayCalculator.restoreState(finalStreetNumbers, finalStreetDelays)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error restaurando estado de calles", e)
                            }
                            
                            try {
                                // Restaurar estado de series
                                if (seriesStats.isNotEmpty()) {
                                    // Asegurarnos de tener exactamente 4 elementos
                                    val seriesNumbers = seriesStats.map { it.number }.toMutableList()
                                    val seriesDelays = seriesStats.map { it.delay }.toMutableList()
                                    
                                    // Rellenar con -1 y 0 hasta tener 4 elementos
                                    while (seriesNumbers.size < 4) {
                                        seriesNumbers.add(-1)
                                        seriesDelays.add(0)
                                    }
                                    // Tomar solo los primeros 4 elementos si hay más
                                    val finalSeriesNumbers = seriesNumbers.take(4).toIntArray()
                                    val finalSeriesDelays = seriesDelays.take(4).toIntArray()
                                    
                                    Log.d(TAG, "Restaurando estado de series - Números: ${finalSeriesNumbers.contentToString()}")
                                    Log.d(TAG, "Restaurando estado de series - Atrasos: ${finalSeriesDelays.contentToString()}")
                                    SeriesDelayCalculator.restoreState(finalSeriesNumbers, finalSeriesDelays)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error restaurando estado de series", e)
                            }
                            
                            try {
                                // Actualizar indicadores visuales
                                val streetDelays = streetStats.map { it.delay }
                                val seriesDelays = seriesStats.map { it.delay }
                                updateIndicatorsFromDelays(streetDelays, seriesDelays)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error actualizando indicadores visuales", e)
                            }
                        } else {
                            Log.d(TAG, "No hay estado previo para restaurar")
                            StreetDelayCalculator.resetDelays()
                            SeriesDelayCalculator.resetDelays()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error procesando DelayStats", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en observeDelayStats", e)
            }
        }
    }

    private fun updateIndicators(indicators: List<Int>) {
        // Crear una lista segura con valores por defecto
        val safeIndicators = List(8) { index -> 
            if (index < indicators.size) indicators[index] else -1 
        }

        // Actualizar los indicadores
        stat1.text = if (safeIndicators[0] >= 0) safeIndicators[0].toString() else "-"
        stat2.text = if (safeIndicators[1] >= 0) safeIndicators[1].toString() else "-"
        stat3.text = if (safeIndicators[2] >= 0) safeIndicators[2].toString() else "-"
        stat4.text = if (safeIndicators[3] >= 0) safeIndicators[3].toString() else "-"
        stat5.text = if (safeIndicators[4] >= 0) safeIndicators[4].toString() else "-"
        stat6.text = if (safeIndicators[5] >= 0) safeIndicators[5].toString() else "-"
        stat7.text = if (safeIndicators[6] >= 0) safeIndicators[6].toString() else "-"
        stat8.text = if (safeIndicators[7] >= 0) safeIndicators[7].toString() else "-"

        // Aplicar colores según el valor
        val indicatorViews = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
        safeIndicators.forEachIndexed { index, delay ->
            if (index < indicatorViews.size) {
                when {
                    delay == 0 -> {
                        // Negro con texto blanco
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.BLACK)
                        indicatorViews[index].setTextColor(android.graphics.Color.WHITE)
                    }
                    delay <= 13 -> {
                        // Gris claro con texto negro
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.LTGRAY)
                        indicatorViews[index].setTextColor(android.graphics.Color.BLACK)
                    }
                    delay <= 17 -> {
                        // Verde oscuro con texto blanco
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.rgb(76, 175, 80))
                        indicatorViews[index].setTextColor(android.graphics.Color.WHITE)
                    }
                    delay <= 30 -> {
                        // Amarillo con texto negro
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.rgb(255, 255, 0))
                        indicatorViews[index].setTextColor(android.graphics.Color.BLACK)
                    }
                    delay <= 40 -> {
                        // Naranja con texto blanco
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.rgb(255, 165, 0))
                        indicatorViews[index].setTextColor(android.graphics.Color.WHITE)
                    }
                    else -> {
                        // Rojo con texto blanco
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.RED)
                        indicatorViews[index].setTextColor(android.graphics.Color.WHITE)
                    }
                }
            }
        }
    }

    private fun updateIndicatorsFromDelays(streetDelays: List<Int>, seriesDelays: List<Int>) {
        Log.d(TAG, "Actualizando indicadores - Street Delays: $streetDelays, Series Delays: $seriesDelays")
        
        // Asegurarnos de que cada lista tenga 4 elementos
        val paddedStreetDelays = List(4) { index -> 
            if (index < streetDelays.size) streetDelays[index] else -1 
        }
        val paddedSeriesDelays = List(4) { index -> 
            if (index < seriesDelays.size) seriesDelays[index] else -1 
        }

        val allDelays = paddedStreetDelays + paddedSeriesDelays
        Log.d(TAG, "Todos los atrasos combinados: $allDelays")
        updateIndicators(allDelays)
    }

    private fun updateProgresionColor(textView: TextView, value: Int) {
        try {
            val color = getColorForValue(value, 8)
            textView.setBackgroundColor(color)
            
            // Calcular el contraste del texto basado en el color de fondo
            val textColor = getContrastTextColor(color)
            textView.setTextColor(textColor)
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando color de progresión", e)
            // Color por defecto en caso de error
            textView.setBackgroundColor(android.graphics.Color.LTGRAY)
            textView.setTextColor(android.graphics.Color.BLACK)
        }
    }

    private fun updateStatistics(numbers: List<RouletteNumber>) {
        if (numbers.isEmpty()) {
            Log.d(TAG, "No hay números, reseteando todo")
            resetAllIndicators()
            return
        }

        try {
            // Obtener el último número ingresado
            val lastNumber = numbers.last().number
            Log.d(TAG, "Último número ingresado: $lastNumber")
            
            // Calcular atrasos actuales
            val currentStreetDelays = StreetDelayCalculator.calculate(numbers)
            val seriesDelays = SeriesDelayCalculator.calculate(numbers)
            
            // Verificar si el último número pertenece a alguna calle
            val streetIndices = getStreetsForNumber(lastNumber)
            Log.d(TAG, "El número $lastNumber pertenece a las calles: $streetIndices")
            
            // Actualizar los indicadores con los nuevos atrasos
            updateIndicatorsFromDelays(currentStreetDelays, seriesDelays)

            // Actualizar los contadores y sus colores
            val counters = StreetDelayCalculator.getCounters()
            counter4.text = counters[0].toString()
            updateCounterColor(counter4, counters[0])
            counter3.text = counters[1].toString()
            updateCounterColor(counter3, counters[1])
            counter2.text = counters[2].toString()
            updateCounterColor(counter2, counters[2])
            counter1.text = counters[3].toString()
            updateCounterColor(counter1, counters[3])

            // Actualizar las progresiones (siempre mínimo 1)
            val progresions = StreetDelayCalculator.getProgresions()
            val progresion1Value = if (progresions[3] <= 0) 1 else progresions[3]
            val progresion2Value = if (progresions[2] <= 0) 1 else progresions[2]
            val progresion3Value = if (progresions[1] <= 0) 1 else progresions[1]
            val progresion4Value = if (progresions[0] <= 0) 1 else progresions[0]
            
            progresion1.text = progresion1Value.toString()  // Posición 1 muestra índice 3
            updateProgresionColor(progresion1, progresion1Value)
            progresion2.text = progresion2Value.toString()  // Posición 2 muestra índice 2
            updateProgresionColor(progresion2, progresion2Value)
            progresion3.text = progresion3Value.toString()  // Posición 3 muestra índice 1
            updateProgresionColor(progresion3, progresion3Value)
            progresion4.text = progresion4Value.toString()  // Posición 4 muestra índice 0
            updateProgresionColor(progresion4, progresion4Value)

            // Guardar el estado actual
            saveDelayStats()
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando estadísticas", e)
        }
    }

    private fun getStreetsForNumber(number: Int): List<Int> {
        val streets = mutableListOf<Int>()
        
        // Solo números del 1 al 12 pertenecen a calles
        if (number in 1..12) {
            // Calle 1 (1,2,3)
            if (number in 1..3) {
                streets.add(0)
                Log.d(TAG, "Número $number pertenece a la calle 1")
            }
            // Calle 2 (4,5,6)
            if (number in 4..6) {
                streets.add(1)
                Log.d(TAG, "Número $number pertenece a la calle 2")
            }
            // Calle 3 (7,8,9)
            if (number in 7..9) {
                streets.add(2)
                Log.d(TAG, "Número $number pertenece a la calle 3")
            }
            // Calle 4 (10,11,12)
            if (number in 10..12) {
                streets.add(3)
                Log.d(TAG, "Número $number pertenece a la calle 4")
            }
        } else {
            Log.d(TAG, "Número $number no pertenece a ninguna calle")
        }
        
        return streets
    }

    private fun saveDelayStats() {
        try {
            // Obtener datos de calles
            val streetNumbers = StreetDelayCalculator.getNumbers()
            val streetDelays = StreetDelayCalculator.getDelays()
            val streetUsedPositions = StreetDelayCalculator.getUsedPositions()
            
            // Obtener datos de series
            val seriesNumbers = SeriesDelayCalculator.getNumbers()
            val seriesDelays = SeriesDelayCalculator.getDelays()
            val seriesUsedPositions = SeriesDelayCalculator.getUsedPositions()
            
            Log.d(TAG, "Guardando estado calles - Números: ${streetNumbers.contentToString()}")
            Log.d(TAG, "Guardando estado calles - Atrasos: ${streetDelays.contentToString()}")
            Log.d(TAG, "Guardando estado calles - Posiciones usadas: $streetUsedPositions")
            Log.d(TAG, "Guardando estado series - Números: ${seriesNumbers.contentToString()}")
            Log.d(TAG, "Guardando estado series - Atrasos: ${seriesDelays.contentToString()}")
            Log.d(TAG, "Guardando estado series - Posiciones usadas: $seriesUsedPositions")

            // Crear lista combinada de DelayStats
            val delayStats = mutableListOf<DelayStats>()
            
            // Agregar stats de calles solo si hay posiciones usadas
            if (streetUsedPositions > 0) {
                for (i in 0 until streetUsedPositions) {
                    delayStats.add(DelayStats(
                        position = i,
                        number = streetNumbers[i],
                        delay = streetDelays[i],
                        type = "street"
                    ))
                }
            }
            
            // Agregar stats de series solo si hay posiciones usadas
            if (seriesUsedPositions > 0) {
                for (i in 0 until seriesUsedPositions) {
                    delayStats.add(DelayStats(
                        position = i,
                        number = seriesNumbers[i],
                        delay = seriesDelays[i],
                        type = "series"
                    ))
                }
            }
            
            Log.d(TAG, "Total de DelayStats a guardar: ${delayStats.size}")
            
            if (delayStats.isNotEmpty()) {
                viewModel.updateDelayStats(delayStats)
            } else {
                Log.d(TAG, "No hay stats para guardar, saltando actualización")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar DelayStats", e)
            // No dejamos que el error se propague
        }
    }

    override fun onPause() {
        super.onPause()
        saveIndicatorsState()
    }

    private fun saveIndicatorsState() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Guardar los valores de los indicadores
        val indicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
        indicators.forEachIndexed { index, textView ->
            val value = textView.text.toString()
            if (value != getString(R.string.empty_stat)) {
                editor.putString("indicator_$index", value)
                editor.putInt("indicator_${index}_bg", textView.backgroundTintList?.defaultColor ?: android.graphics.Color.WHITE)
                editor.putInt("indicator_${index}_text", textView.currentTextColor)
            }
        }
        
        // Guardar los contadores secundarios
        editor.putString("column1Counter2", column1Counter2.text.toString())
        editor.putString("column2Counter2", column2Counter2.text.toString())
        editor.putString("column3Counter2", column3Counter2.text.toString())
        
        editor.apply()
        Log.d(TAG, "Estado de indicadores guardado en SharedPreferences")
    }

    private fun loadSavedIndicators() {
        try {
            val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            
            // Cargar los indicadores
            val indicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
            indicators.forEachIndexed { index, textView ->
                try {
                    val savedValue = prefs.getString("indicator_$index", null)
                    if (savedValue != null) {
                        textView.text = savedValue
                        val bgColor = prefs.getInt("indicator_${index}_bg", android.graphics.Color.WHITE)
                        textView.setBackgroundColor(bgColor)
                        val textColor = prefs.getInt("indicator_${index}_text", android.graphics.Color.BLACK)
                        textView.setTextColor(textColor)
                        Log.d(TAG, "Indicador $index restaurado: valor=$savedValue, bg=$bgColor, text=$textColor")
                    } else {
                        textView.text = getString(R.string.empty_stat)
                        textView.setBackgroundColor(android.graphics.Color.WHITE)
                        textView.setTextColor(android.graphics.Color.BLACK)
                        Log.d(TAG, "Indicador $index no tenía valor guardado, usando valores por defecto")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error al restaurar indicador $index", e)
                    textView.text = getString(R.string.empty_stat)
                    textView.setBackgroundColor(android.graphics.Color.WHITE)
                    textView.setTextColor(android.graphics.Color.BLACK)
                }
            }
            Log.d(TAG, "Carga de indicadores completada")
            
            // Cargar los contadores secundarios
            try {
                val savedColumn1Counter2 = prefs.getString("column1Counter2", "0")
                val savedColumn2Counter2 = prefs.getString("column2Counter2", "0")
                val savedColumn3Counter2 = prefs.getString("column3Counter2", "0")
                
                val value1 = savedColumn1Counter2?.toIntOrNull() ?: 0
                val value2 = savedColumn2Counter2?.toIntOrNull() ?: 0
                val value3 = savedColumn3Counter2?.toIntOrNull() ?: 0
                
                column1Counter2.text = value1.toString()
                column2Counter2.text = value2.toString()
                column3Counter2.text = value3.toString()
                
                updateCounterColor(column1Counter2, value1)
                updateCounterColor(column2Counter2, value2)
                updateCounterColor(column3Counter2, value3)
                
                Log.d(TAG, "Contadores secundarios restaurados: COL1_2=$value1, COL2_2=$value2, COL3_2=$value3")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar contadores secundarios", e)
                column1Counter2.text = "0"
                column2Counter2.text = "0"
                column3Counter2.text = "0"
                updateCounterColor(column1Counter2, 0)
                updateCounterColor(column2Counter2, 0)
                updateCounterColor(column3Counter2, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar indicadores", e)
            // Si falla la carga, resetear todo
            val indicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
            indicators.forEach { 
                it.text = getString(R.string.empty_stat)
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }
            
            // Resetear contadores secundarios
            column1Counter2.text = "0"
            column2Counter2.text = "0"
            column3Counter2.text = "0"
            updateCounterColor(column1Counter2, 0)
            updateCounterColor(column2Counter2, 0)
            updateCounterColor(column3Counter2, 0)
        }
    }

    private fun setupNumericKeypad() {
        val context = requireContext()
        
        // Create buttons 1-9
        for (i in 1..9) {
            val button = MaterialButton(context).apply {
                text = i.toString()
                textSize = 20f
                gravity = android.view.Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 110
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 4, 8, 4)
                }
                setOnClickListener { appendNumber(i.toString()) }
            }
            numericKeypad.addView(button)
        }

        // Add clear, 0, and enter buttons in the last row
        val clearButton = MaterialButton(context).apply {
            text = context.getString(R.string.button_clear)
            textSize = 20f
            gravity = android.view.Gravity.CENTER
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 110
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 4, 8, 4)
            }
            setOnClickListener { numberInput.setText("") }
        }
        numericKeypad.addView(clearButton)

        val zeroButton = MaterialButton(context).apply {
            text = context.getString(R.string.button_zero)
            textSize = 20f
            gravity = android.view.Gravity.CENTER
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 110
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 4, 8, 4)
            }
            setOnClickListener { appendNumber("0") }
        }
        numericKeypad.addView(zeroButton)

        val enterButton = MaterialButton(context).apply {
            text = context.getString(R.string.button_enter)
            textSize = 20f
            gravity = android.view.Gravity.CENTER
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 110
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 4, 8, 4)
            }
            setOnClickListener { submitNumber() }
        }
        numericKeypad.addView(enterButton)
    }

    private fun appendNumber(number: String) {
        val currentText = numberInput.text.toString()
        if (currentText.length < 2) { // Limit to 2 digits (0-36)
            numberInput.append(number)
        }
    }

    private fun submitNumber() {
        val number = numberInput.text.toString().toIntOrNull()
        if (number != null && number in 0..36) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.insert(number)
                numberInput.setText("")
            }
        }
    }

    /**
     * Calcula la apuesta necesaria para ganar mínimo TARGET_FICHAS_OBJETIVO fichas netas
     * La apuesta se incrementa progresivamente basándose en las pérdidas acumuladas
     * @param columnCount Cantidad de números distintos en la columna
     * @param totalAccumulated Pérdidas acumuladas hasta el momento
     * @return Fichas por número que debe apostar
     */
    private fun calculateBetAmount(columnCount: Int, totalAccumulated: Int): Int {
        if (columnCount == 0) return 0
        
        // Para ganar mínimo TARGET_FICHAS_OBJETIVO fichas netas, necesito recuperar las pérdidas + TARGET_FICHAS_OBJETIVO
        val targetWin = totalAccumulated + TARGET_FICHAS_OBJETIVO
        
        // Si acierto un pleno, gano: fichasPorNumero × 35
        // Necesito que: fichasPorNumero × 35 >= targetWin
        val minFichasPorNumero = (targetWin + 34) / 35 // Redondeo hacia arriba
        
        return maxOf(1, minFichasPorNumero)
    }

    /**
     * Actualiza los arrays de apuestas cuando cambian los contadores
     */
    private fun updateBetArrays(numbers: List<Int>) {
        // Obtener los últimos 10 números (cambiado de 5 a 10)
        val lastTenNumbers = numbers.takeLast(10)
        
        // Contar cuántos números de cada columna hay en los últimos 10
        val col1CountInLastTen = lastTenNumbers.count { isInColumn1(it) }
        val col2CountInLastTen = lastTenNumbers.count { isInColumn2(it) }
        val col3CountInLastTen = lastTenNumbers.count { isInColumn3(it) }
        
        // Calcular acumulados previos
        val totalAccumulated1 = betCOL1.sumOf { it.betAmount }
        val totalAccumulated2 = betCOL2.sumOf { it.betAmount }
        val totalAccumulated3 = betCOL3.sumOf { it.betAmount }
        
        // Calcular fichas por número basándose en las pérdidas acumuladas
        val fichasPorNumero1 = if (col1CountInLastTen > 0) calculateBetAmount(col1CountInLastTen, totalAccumulated1) else 0
        val fichasPorNumero2 = if (col2CountInLastTen > 0) calculateBetAmount(col2CountInLastTen, totalAccumulated2) else 0
        val fichasPorNumero3 = if (col3CountInLastTen > 0) calculateBetAmount(col3CountInLastTen, totalAccumulated3) else 0
        
        // Calcular apuestas totales (fichas por número × cantidad de números)
        val bet1 = fichasPorNumero1 * col1CountInLastTen
        val bet2 = fichasPorNumero2 * col2CountInLastTen
        val bet3 = fichasPorNumero3 * col3CountInLastTen
        
        // Crear nuevas entradas de apuesta solo si hay números para apostar
        if (col1CountInLastTen > 0) {
            val betInfo1 = BetInfo(col1CountInLastTen, bet1, totalAccumulated1 + bet1)
            betCOL1.add(betInfo1)
        }
        if (col2CountInLastTen > 0) {
            val betInfo2 = BetInfo(col2CountInLastTen, bet2, totalAccumulated2 + bet2)
            betCOL2.add(betInfo2)
        }
        if (col3CountInLastTen > 0) {
            val betInfo3 = BetInfo(col3CountInLastTen, bet3, totalAccumulated3 + bet3)
            betCOL3.add(betInfo3)
        }
        
        // Actualizar visualización de fichas por número
        // Si hay números de la columna, actualizar; si no hay números pero hay acumulado, mantener valor anterior
        if (col1CountInLastTen > 0) {
            betFichasCol1.text = fichasPorNumero1.toString()
            updateBetFichasColor(betFichasCol1, fichasPorNumero1)
        } else if (totalAccumulated1 > 0) {
            // Mantener el valor anterior si hay acumulado
            val lastFichas = if (betCOL1.isNotEmpty()) betCOL1.last().betAmount / maxOf(1, betCOL1.last().columnCount) else 0
            betFichasCol1.text = lastFichas.toString()
            updateBetFichasColor(betFichasCol1, lastFichas)
        } else {
            betFichasCol1.text = "0"
            updateBetFichasColor(betFichasCol1, 0)
        }
        
        if (col2CountInLastTen > 0) {
            betFichasCol2.text = fichasPorNumero2.toString()
            updateBetFichasColor(betFichasCol2, fichasPorNumero2)
        } else if (totalAccumulated2 > 0) {
            val lastFichas = if (betCOL2.isNotEmpty()) betCOL2.last().betAmount / maxOf(1, betCOL2.last().columnCount) else 0
            betFichasCol2.text = lastFichas.toString()
            updateBetFichasColor(betFichasCol2, lastFichas)
        } else {
            betFichasCol2.text = "0"
            updateBetFichasColor(betFichasCol2, 0)
        }
        
        if (col3CountInLastTen > 0) {
            betFichasCol3.text = fichasPorNumero3.toString()
            updateBetFichasColor(betFichasCol3, fichasPorNumero3)
        } else if (totalAccumulated3 > 0) {
            val lastFichas = if (betCOL3.isNotEmpty()) betCOL3.last().betAmount / maxOf(1, betCOL3.last().columnCount) else 0
            betFichasCol3.text = lastFichas.toString()
            updateBetFichasColor(betFichasCol3, lastFichas)
        } else {
            betFichasCol3.text = "0"
            updateBetFichasColor(betFichasCol3, 0)
        }
        
        // Actualizar campos de acumulados de fichas gastadas (incluyendo la apuesta actual si se hace)
        val newTotalAccumulated1 = if (col1CountInLastTen > 0) totalAccumulated1 + bet1 else totalAccumulated1
        val newTotalAccumulated2 = if (col2CountInLastTen > 0) totalAccumulated2 + bet2 else totalAccumulated2
        val newTotalAccumulated3 = if (col3CountInLastTen > 0) totalAccumulated3 + bet3 else totalAccumulated3
        
        accumulatedBetFichasCol1.text = newTotalAccumulated1.toString()
        accumulatedBetFichasCol2.text = newTotalAccumulated2.toString()
        accumulatedBetFichasCol3.text = newTotalAccumulated3.toString()
        
        Log.d("Apuestas", "Últimos 10: $lastTenNumbers")
        Log.d("Apuestas", "COL1: $col1CountInLastTen números, $fichasPorNumero1 fichas/número, total: $bet1, acumulado: $newTotalAccumulated1")
        Log.d("Apuestas", "COL2: $col2CountInLastTen números, $fichasPorNumero2 fichas/número, total: $bet2, acumulado: $newTotalAccumulated2")
        Log.d("Apuestas", "COL3: $col3CountInLastTen números, $fichasPorNumero3 fichas/número, total: $bet3, acumulado: $newTotalAccumulated3")
    }

    /**
     * Aplica color de fondo basado en el valor de fichas por número
     * Gama de colores de frío a calor: amarillo -> naranja -> rojo
     */
    private fun updateBetFichasColor(textView: TextView, value: Int) {
        val color = when (value) {
            0 -> android.graphics.Color.WHITE
            1 -> android.graphics.Color.rgb(255, 255, 0) // Amarillo
            2 -> android.graphics.Color.rgb(255, 200, 0) // Amarillo naranja
            3 -> android.graphics.Color.rgb(255, 150, 0) // Naranja
            4 -> android.graphics.Color.rgb(255, 100, 0) // Naranja rojizo
            5 -> android.graphics.Color.rgb(255, 50, 0) // Rojo naranja
            6 -> android.graphics.Color.rgb(255, 25, 0) // Rojo claro
            7 -> android.graphics.Color.rgb(255, 0, 0) // Rojo intenso
            else -> android.graphics.Color.rgb(200, 0, 0) // Rojo muy intenso
        }
        
        textView.setBackgroundColor(color)
        
        // Forzar texto negro para amarillo, blanco para naranjas y rojos
        val textColor = when (value) {
            0 -> android.graphics.Color.BLACK // Fondo blanco
            1, 2 -> android.graphics.Color.BLACK // Amarillo y amarillo naranja
            else -> android.graphics.Color.WHITE // Naranjas y rojos
        }
        textView.setTextColor(textColor)
    }

    companion object {
        private const val TAG = "InputFragment"
        private const val PREFS_NAME = "RouletteIndicators"
        
        /**
         * CONFIGURACIÓN DE PROGRESIÓN:
         * Para cambiar la agresividad de la progresión, modifica la variable TARGET_FICHAS_OBJETIVO
         * en la clase InputFragment:
         * - Valor actual: 5 fichas (menos agresivo)
         * - Valor anterior: 9 fichas (más agresivo)
         * - Valores recomendados: entre 3-20 fichas
         * 
         * Ejemplo: cambiar "private val TARGET_FICHAS_OBJETIVO = 9" por el valor deseado
         */
        
        fun newInstance() = InputFragment()
    }
} 