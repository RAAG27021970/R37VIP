package com.r37vip.app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.r37vip.app.viewmodels.RouletteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlateFragment : Fragment() {
    private val viewModel: RouletteViewModel by viewModels()
    private lateinit var textViews: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frame = view as FrameLayout
        // Vincular los TextView del 0 al 36 de forma directa
        textViews = List(37) { i ->
            frame.findViewById<TextView>(resources.getIdentifier("tv_plate$i", "id", requireContext().packageName))
        }
        frame.post {
            val centerX = frame.width / 2f
            val centerY = frame.height / 2f
            val radius = (frame.width.coerceAtMost(frame.height) / 2f) - 60f // margen para que no se salgan

            // Orden europeo de la ruleta
            val order = listOf(
                0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
            )
            val total = order.size
            for ((i, num) in order.withIndex()) {
                val tv = textViews[num]
                if (tv != null) {
                    val angle = Math.toRadians((360.0 / total * i) - 90) // -90 para empezar arriba
                    val x = (centerX + radius * Math.cos(angle) - tv.width / 2 + 57).toFloat() // +57px a la derecha
                    val y = (centerY + radius * Math.sin(angle) - tv.height / 2).toFloat()
                    tv.translationX = x - centerX
                    tv.translationY = y - centerY
                }
            }
        }
        // Observar los números y actualizar los colores
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allNumbers.collectLatest { numbers ->
                val counts = IntArray(37) { 0 }
                numbers.forEach { if (it.number in 0..36) counts[it.number]++ }
                for (i in 0..36) {
                    setTableCellColor(textViews[i], counts[i])
                }
            }
        }
    }

    // Copiado de TableFragment
    private fun setTableCellColor(textView: TextView, value: Int) {
        when {
            value == 0 -> {
                textView.setBackgroundColor(Color.BLACK)
                textView.setTextColor(Color.BLACK)
            }
            value in 1..2 -> {
                textView.setBackgroundColor(Color.parseColor("#90EE90")) // Verde claro
                textView.setTextColor(Color.BLACK)
            }
            value in 3..5 -> {
                textView.setBackgroundColor(Color.YELLOW)
                textView.setTextColor(Color.BLACK)
            }
            value == 6 -> {
                textView.setBackgroundColor(Color.RED)
                textView.setTextColor(Color.WHITE)
            }
            value >= 7 -> {
                textView.setBackgroundColor(Color.parseColor("#001F54")) // Azul oscuro
                textView.setTextColor(Color.WHITE)
            }
            else -> {
                textView.setBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    companion object {
        fun newInstance(): PlateFragment {
            return PlateFragment()
        }
    }
} 