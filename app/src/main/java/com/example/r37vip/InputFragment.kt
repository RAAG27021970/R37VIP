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

    // Contadores
    private lateinit var counter1: TextView
    private lateinit var counter2: TextView
    private lateinit var counter3: TextView
    private lateinit var counter4: TextView

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

                // Initialize counters
                counter1 = view.findViewById(R.id.counter1)
                counter2 = view.findViewById(R.id.counter2)
                counter3 = view.findViewById(R.id.counter3)
                counter4 = view.findViewById(R.id.counter4)
                
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

    private fun updateLastNumbers(numbers: List<RouletteNumber>) {
        try {
            // Limpiar todos los números primero
            lastNumber1.text = "-"
            lastNumber2.text = "-"
            lastNumber3.text = "-"
            lastNumber4.text = "-"
            
            if (numbers.size >= 4) {
                // Tomamos los últimos 4 números en orden cronológico (del más antiguo al más reciente)
                val lastFour = numbers.takeLast(4)
                lastNumber1.text = lastFour[0].number.toString() // El más antiguo
                lastNumber2.text = lastFour[1].number.toString()
                lastNumber3.text = lastFour[2].number.toString()
                lastNumber4.text = lastFour[3].number.toString() // El más reciente
            } else {
                // Si hay menos de 4 números, mostramos los que haya
                numbers.takeLast(4).forEachIndexed { index, number ->
                    when (index) {
                        0 -> lastNumber1.text = number.number.toString()
                        1 -> lastNumber2.text = number.number.toString()
                        2 -> lastNumber3.text = number.number.toString()
                        3 -> lastNumber4.text = number.number.toString()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando últimos números", e)
        }
    }

    private fun resetAllIndicators() {
        // Resetear los calculadores
        StreetDelayCalculator.resetDelays()
        SeriesDelayCalculator.resetDelays()
        
        // Resetear los indicadores
        val allIndicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
        allIndicators.forEach { 
            it.text = getString(R.string.empty_stat)
            it.setBackgroundColor(android.graphics.Color.WHITE)
            it.setTextColor(android.graphics.Color.BLACK)
        }
        
        // Resetear los últimos números
        lastNumber1.text = "-"
        lastNumber2.text = "-"
        lastNumber3.text = "-"
        lastNumber4.text = "-"

        // Resetear los contadores
        counter4.text = "0"
        counter3.text = "0"
        counter2.text = "0"
        counter1.text = "0"

        // Resetear los calculadores del ViewModel
        viewModel.resetCalculators()
        
        // Guardar el estado reseteado
        saveDelayStats()
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

            // Actualizar los contadores
            val counters = StreetDelayCalculator.getCounters()
            counter4.text = counters[0].toString()
            counter3.text = counters[1].toString()
            counter2.text = counters[2].toString()
            counter1.text = counters[3].toString()

            // Guardar el estado actual
            saveDelayStats()
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estadísticas", e)
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
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar indicadores", e)
            // Si falla la carga, resetear todo
            val indicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
            indicators.forEach { 
                it.text = getString(R.string.empty_stat)
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }
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