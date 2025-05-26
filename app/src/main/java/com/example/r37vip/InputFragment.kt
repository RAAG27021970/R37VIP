package com.example.r37vip

import android.os.Bundle
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
import com.example.r37vip.stats.StreetDelayCalculator
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
        super.onViewCreated(view, savedInstanceState)
        
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

        setupNumericKeypad()
        observeNumbers()
    }

    private fun observeNumbers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allNumbers.collectLatest { numbers ->
                updateStatistics(numbers)
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
                Toast.makeText(context, 
                    getString(R.string.number_saved, number), 
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, 
                getString(R.string.invalid_number), 
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatistics(numbers: List<RouletteNumber>) {
        // Si no hay números, reiniciamos los atrasos y mostramos guiones
        if (numbers.isEmpty()) {
            StreetDelayCalculator.resetDelays()
            val indicators = listOf(stat1, stat2, stat3, stat4)
            indicators.forEach { 
                it.text = getString(R.string.empty_stat)
                it.setBackgroundColor(android.graphics.Color.WHITE)
                it.setTextColor(android.graphics.Color.BLACK)
            }
            return
        }

        val delays = StreetDelayCalculator.calculate(numbers)
        
        // Actualizamos los primeros 4 indicadores con los atrasos
        val indicators = listOf(stat1, stat2, stat3, stat4)
        
        // Mostramos los atrasos calculados
        delays.forEachIndexed { index, delay ->
            if (index < indicators.size) {
                indicators[index].text = delay.toString()
                
                // Aplicamos colores según el rango
                when {
                    delay <= 17 -> {
                        // Verde claro con texto negro
                        indicators[index].setBackgroundColor(android.graphics.Color.rgb(144, 238, 144))
                        indicators[index].setTextColor(android.graphics.Color.BLACK)
                    }
                    delay <= 30 -> {
                        // Amarillo con texto negro
                        indicators[index].setBackgroundColor(android.graphics.Color.rgb(255, 255, 0))
                        indicators[index].setTextColor(android.graphics.Color.BLACK)
                    }
                    delay <= 40 -> {
                        // Naranja con texto blanco
                        indicators[index].setBackgroundColor(android.graphics.Color.rgb(255, 165, 0))
                        indicators[index].setTextColor(android.graphics.Color.WHITE)
                    }
                    else -> {
                        // Rojo con texto blanco
                        indicators[index].setBackgroundColor(android.graphics.Color.RED)
                        indicators[index].setTextColor(android.graphics.Color.WHITE)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = InputFragment()
    }
} 