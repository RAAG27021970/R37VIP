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

    private fun resetAllIndicators() {
        StreetDelayCalculator.resetDelays()
        SeriesDelayCalculator.resetDelays()
        
        val allIndicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
        allIndicators.forEach { 
            it.text = getString(R.string.empty_stat)
            it.setBackgroundColor(android.graphics.Color.WHITE)
            it.setTextColor(android.graphics.Color.BLACK)
        }
        
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
                                    val streetNumbers = streetStats.map { it.number }.toIntArray()
                                    val streetDelays = streetStats.map { it.delay }.toIntArray()
                                    Log.d(TAG, "Restaurando estado de calles - Números: ${streetNumbers.contentToString()}")
                                    Log.d(TAG, "Restaurando estado de calles - Atrasos: ${streetDelays.contentToString()}")
                                    StreetDelayCalculator.restoreState(streetNumbers, streetDelays)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error restaurando estado de calles", e)
                            }
                            
                            try {
                                // Restaurar estado de series
                                if (seriesStats.isNotEmpty()) {
                                    val seriesNumbers = seriesStats.map { it.number }.toIntArray()
                                    val seriesDelays = seriesStats.map { it.delay }.toIntArray()
                                    Log.d(TAG, "Restaurando estado de series - Números: ${seriesNumbers.contentToString()}")
                                    Log.d(TAG, "Restaurando estado de series - Atrasos: ${seriesDelays.contentToString()}")
                                    SeriesDelayCalculator.restoreState(seriesNumbers, seriesDelays)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error restaurando estado de series", e)
                            }
                            
                            try {
                                // Actualizar indicadores visuales
                                val streetDelays = streetStats.map { it.delay }
                                val seriesDelays = seriesStats.map { it.delay }
                                updateIndicators(streetDelays, seriesDelays)
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

    private fun updateIndicators(streetDelays: List<Int>, seriesDelays: List<Int>) {
        val streetIndicators = listOf(stat1, stat2, stat3, stat4)
        val seriesIndicators = listOf(stat5, stat6, stat7, stat8)
        
        // Actualizar indicadores de calles
        streetDelays.forEachIndexed { index, delay ->
            if (index < streetIndicators.size) {
                updateSingleIndicator(streetIndicators[index], delay)
            }
        }

        // Actualizar indicadores de series
        seriesDelays.forEachIndexed { index, delay ->
            if (index < seriesIndicators.size) {
                updateSingleIndicator(seriesIndicators[index], delay)
            }
        }
    }

    private fun updateSingleIndicator(indicator: TextView, delay: Int) {
        indicator.text = delay.toString()
        
        // Aplicamos colores según el rango
        when {
            delay <= 17 -> {
                // Verde claro con texto negro
                indicator.setBackgroundColor(android.graphics.Color.rgb(144, 238, 144))
                indicator.setTextColor(android.graphics.Color.BLACK)
            }
            delay <= 30 -> {
                // Amarillo con texto negro
                indicator.setBackgroundColor(android.graphics.Color.rgb(255, 255, 0))
                indicator.setTextColor(android.graphics.Color.BLACK)
            }
            delay <= 40 -> {
                // Naranja con texto blanco
                indicator.setBackgroundColor(android.graphics.Color.rgb(255, 165, 0))
                indicator.setTextColor(android.graphics.Color.WHITE)
            }
            else -> {
                // Rojo con texto blanco
                indicator.setBackgroundColor(android.graphics.Color.RED)
                indicator.setTextColor(android.graphics.Color.WHITE)
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

    private fun updateStatistics(numbers: List<RouletteNumber>) {
        Log.d(TAG, "Actualizando estadísticas con ${numbers.size} números")
        
        // Si no hay números, reiniciamos los atrasos y mostramos guiones
        if (numbers.isEmpty()) {
            Log.d(TAG, "No hay números, reseteando todo")
            StreetDelayCalculator.resetDelays()
            SeriesDelayCalculator.resetDelays()
            
            val allIndicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
            allIndicators.forEach { 
                it.text = getString(R.string.empty_stat)
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }
            
            // No guardamos el estado cuando no hay números
            return
        }

        try {
            // Calcular atrasos de calles
            val streetDelays = StreetDelayCalculator.calculate(numbers)
            // Calcular atrasos de series
            val seriesDelays = SeriesDelayCalculator.calculate(numbers)
            
            Log.d(TAG, "Atrasos de calles calculados: $streetDelays")
            Log.d(TAG, "Atrasos de series calculados: $seriesDelays")
            
            // Actualizamos los indicadores con los nuevos atrasos
            updateIndicators(streetDelays, seriesDelays)

            // Guardar el estado actual
            saveDelayStats()
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estadísticas", e)
            // No dejamos que el error se propague
        }
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
        saveIndicatorsState() // Guardar estado al salir
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
                
                // Guardar el color de fondo
                editor.putInt("indicator_${index}_bg", textView.backgroundTintList?.defaultColor ?: android.graphics.Color.WHITE)
                
                // Guardar el color del texto
                editor.putInt("indicator_${index}_text", textView.currentTextColor)
            }
        }
        editor.apply()
        Log.d(TAG, "Estado de indicadores guardado en SharedPreferences")
    }

    private fun loadSavedIndicators() {
        try {
            val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val indicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
            
            Log.d(TAG, "Iniciando carga de indicadores guardados")
            
            // Cargar los valores guardados
            indicators.forEachIndexed { index, textView ->
                try {
                    val savedValue = prefs.getString("indicator_$index", null)
                    if (savedValue != null) {
                        textView.text = savedValue
                        
                        // Restaurar color de fondo
                        val bgColor = prefs.getInt("indicator_${index}_bg", android.graphics.Color.WHITE)
                        textView.setBackgroundColor(bgColor)
                        
                        // Restaurar color del texto
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
                    // Si falla un indicador, lo reseteamos a valores por defecto
                    textView.text = getString(R.string.empty_stat)
                    textView.setBackgroundColor(android.graphics.Color.WHITE)
                    textView.setTextColor(android.graphics.Color.BLACK)
                }
            }
            Log.d(TAG, "Carga de indicadores completada")
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar indicadores", e)
            // Si falla todo, reseteamos todos los indicadores
            val indicators = listOf(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8)
            indicators.forEach { 
                it.text = getString(R.string.empty_stat)
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }
        }
    }

    companion object {
        private const val TAG = "InputFragment"
        private const val PREFS_NAME = "RouletteIndicators"
        fun newInstance() = InputFragment()
    }
} 