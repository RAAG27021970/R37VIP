package com.example.r37vip

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
import com.example.r37vip.data.RouletteNumber
import com.example.r37vip.data.DelayStats
import com.example.r37vip.stats.StreetDelayCalculator
import com.example.r37vip.stats.SeriesDelayCalculator
import com.example.r37vip.viewmodels.RouletteViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InputFragment : Fragment() {
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

    // Progresiones
    private lateinit var progresion1: TextView
    private lateinit var progresion2: TextView
    private lateinit var progresion3: TextView
    private lateinit var progresion4: TextView

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
                textView.setTextColor(android.graphics.Color.BLACK)
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
            val lastElevenForLogic = numbers.takeLast(11)
            updateColumnCounters(lastElevenForLogic.map { it.number })
            
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando últimos números", e)
        }
    }

    private fun updateColumnCounters(numbers: List<Int>) {
        try {
            // Obtener valores anteriores de los contadores
            val previousCount1 = column1Counter.text.toString().toIntOrNull() ?: 0
            val previousCount2 = column2Counter.text.toString().toIntOrNull() ?: 0
            val previousCount3 = column3Counter.text.toString().toIntOrNull() ?: 0
            
            // Columna 1
            val col1Numbers = numbers.filter { isInColumn1(it) }
            val count1 = calculateCountAfterLastRepetition(col1Numbers)
            column1Counter.text = count1.toString()
            updateCounterColor(column1Counter, count1)

            // Columna 2
            val col2Numbers = numbers.filter { isInColumn2(it) }
            val count2 = calculateCountAfterLastRepetition(col2Numbers)
            column2Counter.text = count2.toString()
            updateCounterColor(column2Counter, count2)

            // Columna 3
            val col3Numbers = numbers.filter { isInColumn3(it) }
            val count3 = calculateCountAfterLastRepetition(col3Numbers)
            column3Counter.text = count3.toString()
            updateCounterColor(column3Counter, count3)

            // Detectar repeticiones y actualizar contadores secundarios
            updateSecondaryCounters(col1Numbers, col2Numbers, col3Numbers)

            Log.d("Contadores", "Cinta: $numbers -> Col1: $count1, Col2: $count2, Col3: $count3")

        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando contadores de columnas", e)
        }
    }

    private fun updateSecondaryCounters(col1Numbers: List<Int>, col2Numbers: List<Int>, col3Numbers: List<Int>) {
        try {
            // Obtener valores actuales de los contadores secundarios
            val currentValue1 = column1Counter2.text.toString().toIntOrNull() ?: 0
            val currentValue2 = column2Counter2.text.toString().toIntOrNull() ?: 0
            val currentValue3 = column3Counter2.text.toString().toIntOrNull() ?: 0
            
            // Detectar si hay una nueva repetición en cada columna
            val hasNewRepetition1 = detectNewRepetition(col1Numbers)
            val hasNewRepetition2 = detectNewRepetition(col2Numbers)
            val hasNewRepetition3 = detectNewRepetition(col3Numbers)
            
            // Actualizar contadores secundarios solo si hay nueva repetición
            val newValue1 = if (hasNewRepetition1) currentValue1 + 1 else currentValue1
            val newValue2 = if (hasNewRepetition2) currentValue2 + 1 else currentValue2
            val newValue3 = if (hasNewRepetition3) currentValue3 + 1 else currentValue3
            
            column1Counter2.text = newValue1.toString()
            column2Counter2.text = newValue2.toString()
            column3Counter2.text = newValue3.toString()
            
            // Aplicar colores a los contadores secundarios
            updateCounterColor(column1Counter2, newValue1)
            updateCounterColor(column2Counter2, newValue2)
            updateCounterColor(column3Counter2, newValue3)
            
            if (hasNewRepetition1 || hasNewRepetition2 || hasNewRepetition3) {
                Log.d("Contadores", "Nueva repetición detectada - Col1: $hasNewRepetition1, Col2: $hasNewRepetition2, Col3: $hasNewRepetition3")
                Log.d("Contadores", "Contadores secundarios actualizados - COL1_2: $newValue1, COL2_2: $newValue2, COL3_2: $newValue3")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando contadores secundarios", e)
        }
    }

    private fun detectNewRepetition(colNumbers: List<Int>): Boolean {
        if (colNumbers.size < 2) return false
        
        // Verificar si el último número ya apareció antes en la secuencia
        val lastNumber = colNumbers.last()
        val previousNumbers = colNumbers.dropLast(1)
        
        return lastNumber in previousNumbers
    }

    private fun getColorForValue(value: Int, maxValue: Int = 20): Int {
        // Normalizar el valor entre 0 y 1
        val normalizedValue = (value.toFloat() / maxValue).coerceIn(0f, 1f)
        
        // Interpolar entre verde (0) y rojo (1)
        val red = (255 * normalizedValue).toInt()
        val green = (255 * (1 - normalizedValue)).toInt()
        val blue = 0
        
        return android.graphics.Color.rgb(red, green, blue)
    }

    private fun updateCounterColor(textView: TextView, value: Int) {
        try {
            val color = getColorForValue(value)
            textView.setBackgroundColor(color)
            
            // Ajustar el color del texto para mejor contraste
            val textColor = if (value > 10) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            textView.setTextColor(textColor)
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando color del contador", e)
        }
    }

    /**
     * Calcula el tamaño de la secuencia de números únicos después de la última repetición.
     */
    private fun calculateCountAfterLastRepetition(colNumbers: List<Int>): Int {
        if (colNumbers.isEmpty()) {
            return 0
        }

        // Encuentra todos los números que aparecen más de una vez.
        val duplicates = colNumbers.groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .keys

        // Si no hay duplicados, el contador es simplemente el total de números de esa columna.
        if (duplicates.isEmpty()) {
            return colNumbers.size
        }

        // Encuentra el índice de la última vez que apareció CUALQUIERA de los números duplicados.
        val lastDuplicateIndex = colNumbers.indexOfLast { it in duplicates }

        // El contador es la cantidad de elementos que hay DESPUÉS de ese índice.
        return colNumbers.size - 1 - lastDuplicateIndex
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

            // Reset counter colors
            val counterViews = listOf(counter1, counter2, counter3, counter4)
            counterViews.forEach {
                it.setBackgroundColor(android.graphics.Color.rgb(26, 35, 126)) // Color azul original
                it.setTextColor(android.graphics.Color.WHITE)
            }

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

            // Reset progresions
            progresion1.text = getString(R.string.empty_stat)
            progresion2.text = getString(R.string.empty_stat)
            progresion3.text = getString(R.string.empty_stat)
            progresion4.text = getString(R.string.empty_stat)

            // Reset progresion colors
            val progresionViews = listOf(progresion1, progresion2, progresion3, progresion4)
            progresionViews.forEach {
                it.setBackgroundColor(android.graphics.Color.rgb(74, 20, 140)) // Color púrpura original
                it.setTextColor(android.graphics.Color.WHITE)
            }
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
                        // Verde claro con texto negro
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.rgb(144, 238, 144))
                        indicatorViews[index].setTextColor(android.graphics.Color.BLACK)
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
        when {
            value in 1..4 -> {
                // Verde oscuro
                textView.setBackgroundColor(android.graphics.Color.rgb(76, 175, 80))
                textView.setTextColor(android.graphics.Color.WHITE)
            }
            value in 5..9 -> {
                // Verde medio
                textView.setBackgroundColor(android.graphics.Color.rgb(129, 199, 132))
                textView.setTextColor(android.graphics.Color.BLACK)
            }
            value in 10..13 -> {
                // Verde claro
                textView.setBackgroundColor(android.graphics.Color.rgb(165, 214, 167))
                textView.setTextColor(android.graphics.Color.BLACK)
            }
            value in 14..16 -> {
                // Verde muy claro
                textView.setBackgroundColor(android.graphics.Color.rgb(200, 230, 201))
                textView.setTextColor(android.graphics.Color.BLACK)
            }
            value in 17..18 -> {
                // Amarillo
                textView.setBackgroundColor(android.graphics.Color.rgb(255, 235, 59))
                textView.setTextColor(android.graphics.Color.BLACK)
            }
            value in 19..20 -> {
                // Naranja
                textView.setBackgroundColor(android.graphics.Color.rgb(255, 152, 0))
                textView.setTextColor(android.graphics.Color.WHITE)
            }
            value in 21..22 -> {
                // Rojo claro
                textView.setBackgroundColor(android.graphics.Color.rgb(244, 67, 54))
                textView.setTextColor(android.graphics.Color.WHITE)
            }
            value >= 23 -> {
                // Rojo intenso
                textView.setBackgroundColor(android.graphics.Color.rgb(198, 40, 40))
                textView.setTextColor(android.graphics.Color.WHITE)
            }
            else -> {  // value == 0
                textView.setBackgroundColor(android.graphics.Color.LTGRAY)
                textView.setTextColor(android.graphics.Color.BLACK)
            }
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

            // Actualizar las progresiones
            val progresions = StreetDelayCalculator.getProgresions()
            progresion1.text = progresions[3].toString()  // Posición 1 muestra índice 3
            updateProgresionColor(progresion1, progresions[3])
            progresion2.text = progresions[2].toString()  // Posición 2 muestra índice 2
            updateProgresionColor(progresion2, progresions[2])
            progresion3.text = progresions[1].toString()  // Posición 3 muestra índice 1
            updateProgresionColor(progresion3, progresions[1])
            progresion4.text = progresions[0].toString()  // Posición 4 muestra índice 0
            updateProgresionColor(progresion4, progresions[0])

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
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }
                setOnClickListener { appendNumber(i.toString()) }
            }
            numericKeypad.addView(button)
        }

        // Add clear, 0, and enter buttons in the last row
        val clearButton = MaterialButton(context).apply {
            text = context.getString(R.string.button_clear)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { numberInput.setText("") }
        }
        numericKeypad.addView(clearButton)

        val zeroButton = MaterialButton(context).apply {
            text = context.getString(R.string.button_zero)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { appendNumber("0") }
        }
        numericKeypad.addView(zeroButton)

        val enterButton = MaterialButton(context).apply {
            text = context.getString(R.string.button_enter)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
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

    companion object {
        private const val TAG = "InputFragment"
        private const val PREFS_NAME = "RouletteIndicators"
        fun newInstance() = InputFragment()
    }
} 