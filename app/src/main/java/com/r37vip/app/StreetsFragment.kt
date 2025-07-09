package com.r37vip.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.r37vip.app.data.RouletteNumber
import com.r37vip.app.stats.StreetDelayCalculator
import com.r37vip.app.stats.SeriesDelayCalculator
import com.r37vip.app.viewmodels.RouletteViewModel
import com.r37vip.app.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StreetsFragment : Fragment() {

    companion object {
        private const val TAG = "StreetsFragment"
        
        fun newInstance(): StreetsFragment {
            return StreetsFragment()
        }
    }

    // Statistics TextViews (only 4 for Street tab)
    private lateinit var stat1: TextView
    private lateinit var stat2: TextView
    private lateinit var stat3: TextView
    private lateinit var stat4: TextView

    // Counters
    private lateinit var counter1: TextView
    private lateinit var counter2: TextView
    private lateinit var counter3: TextView
    private lateinit var counter4: TextView

    // Progressions
    private lateinit var progresion1: TextView
    private lateinit var progresion2: TextView
    private lateinit var progresion3: TextView
    private lateinit var progresion4: TextView

    private val viewModel: RouletteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            // Initialize statistics TextViews (only 4)
            stat1 = view.findViewById(R.id.stat1)
            stat2 = view.findViewById(R.id.stat2)
            stat3 = view.findViewById(R.id.stat3)
            stat4 = view.findViewById(R.id.stat4)

            // Initialize counters
            counter1 = view.findViewById(R.id.counter1)
            counter2 = view.findViewById(R.id.counter2)
            counter3 = view.findViewById(R.id.counter3)
            counter4 = view.findViewById(R.id.counter4)

            // Initialize progressions
            progresion1 = view.findViewById(R.id.progresion1)
            progresion2 = view.findViewById(R.id.progresion2)
            progresion3 = view.findViewById(R.id.progresion3)
            progresion4 = view.findViewById(R.id.progresion4)

            // Initialize all values
            initializeValues()
            
            // Observe ViewModel data
            observeViewModel()
            
            Log.d(TAG, "StreetsFragment initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing StreetsFragment", e)
        }
    }

    private fun initializeValues() {
        // Initialize statistics
        stat1.text = "-"
        stat2.text = "-"
        stat3.text = "-"
        stat4.text = "-"

        // Initialize counters
        counter1.text = "0"
        counter2.text = "0"
        counter3.text = "0"
        counter4.text = "0"

        // Initialize progressions (always start from 1)
        progresion1.text = "1"
        progresion2.text = "1"
        progresion3.text = "1"
        progresion4.text = "1"
    }

    private fun observeViewModel() {
        // Observe numbers to update statistics
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(TAG, "Iniciando observación de números en StreetsFragment")
                viewModel.allNumbers.collectLatest { numbers ->
                    try {
                        Log.d(TAG, "Recibidos ${numbers.size} números en StreetsFragment")
                        if (numbers.isNotEmpty()) {
                            Log.d(TAG, "Actualizando estadísticas en StreetsFragment")
                            updateStatistics(numbers)
                        } else {
                            Log.d(TAG, "Reseteando indicadores en StreetsFragment")
                            resetAllIndicators()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing numbers", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error observing numbers", e)
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
            
            Log.d(TAG, "Street Delays calculados: $currentStreetDelays")
            Log.d(TAG, "Series Delays calculados: $seriesDelays")
            
            // Actualizar los indicadores con los nuevos atrasos
            updateIndicatorsFromDelays(currentStreetDelays, seriesDelays)

            // Actualizar los contadores y sus colores
            val counters = StreetDelayCalculator.getCounters()
            Log.d(TAG, "Counters obtenidos: ${counters.contentToString()}")
            
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
            Log.d(TAG, "Progresions obtenidas: ${progresions.contentToString()}")
            
            val progresion1Value = if (progresions[3] <= 0) 1 else progresions[3]
            val progresion2Value = if (progresions[2] <= 0) 1 else progresions[2]
            val progresion3Value = if (progresions[1] <= 0) 1 else progresions[1]
            val progresion4Value = if (progresions[0] <= 0) 1 else progresions[0]
            
            progresion1.text = progresion1Value.toString()
            updateProgresionColor(progresion1, progresion1Value)
            progresion2.text = progresion2Value.toString()
            updateProgresionColor(progresion2, progresion2Value)
            progresion3.text = progresion3Value.toString()
            updateProgresionColor(progresion3, progresion3Value)
            progresion4.text = progresion4Value.toString()
            updateProgresionColor(progresion4, progresion4Value)

            Log.d(TAG, "Estadísticas actualizadas exitosamente")

        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando estadísticas", e)
        }
    }

    private fun updateIndicators(indicators: List<Int>) {
        // Crear una lista segura con valores por defecto (solo 4 elementos para Street)
        val safeIndicators = List(4) { index -> 
            if (index < indicators.size) indicators[index] else -1 
        }

        // Actualizar solo los 4 indicadores que existen en Street
        stat1.text = if (safeIndicators[0] >= 0) safeIndicators[0].toString() else "-"
        stat2.text = if (safeIndicators[1] >= 0) safeIndicators[1].toString() else "-"
        stat3.text = if (safeIndicators[2] >= 0) safeIndicators[2].toString() else "-"
        stat4.text = if (safeIndicators[3] >= 0) safeIndicators[3].toString() else "-"

        // Aplicar colores según el valor (solo 4 indicadores)
        val indicatorViews = listOf(stat1, stat2, stat3, stat4)
        safeIndicators.forEachIndexed { index, delay ->
            if (index < indicatorViews.size) {
                when {
                    delay == 0 -> {
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.BLACK)
                        indicatorViews[index].setTextColor(android.graphics.Color.WHITE)
                    }
                    delay <= 13 -> {
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.LTGRAY)
                        indicatorViews[index].setTextColor(android.graphics.Color.BLACK)
                    }
                    delay <= 17 -> {
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.rgb(76, 175, 80))
                        indicatorViews[index].setTextColor(android.graphics.Color.WHITE)
                    }
                    delay <= 30 -> {
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.rgb(255, 255, 0))
                        indicatorViews[index].setTextColor(android.graphics.Color.BLACK)
                    }
                    delay <= 40 -> {
                        indicatorViews[index].setBackgroundColor(android.graphics.Color.rgb(255, 165, 0))
                        indicatorViews[index].setTextColor(android.graphics.Color.WHITE)
                    }
                    else -> {
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

    private fun resetAllIndicators() {
        val indicators = listOf(stat1, stat2, stat3, stat4)
        indicators.forEach { indicator ->
            indicator.text = "-"
            indicator.setBackgroundColor(android.graphics.Color.WHITE)
            indicator.setTextColor(android.graphics.Color.BLACK)
        }

        // Reset counters using new color scheme
        counter1.text = "0"
        counter2.text = "0"
        counter3.text = "0"
        counter4.text = "0"
        updateCounterColor(counter1, 0)
        updateCounterColor(counter2, 0)
        updateCounterColor(counter3, 0)
        updateCounterColor(counter4, 0)

        // Reset progressions using new color scheme (always start from 1)
        progresion1.text = "1"
        progresion2.text = "1"
        progresion3.text = "1"
        progresion4.text = "1"
        updateProgresionColor(progresion1, 1)
        updateProgresionColor(progresion2, 1)
        updateProgresionColor(progresion3, 1)
        updateProgresionColor(progresion4, 1)
    }

} 