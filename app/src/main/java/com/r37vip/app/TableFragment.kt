package com.r37vip.app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.r37vip.app.viewmodels.RouletteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TableFragment : Fragment() {

    private val viewModel: RouletteViewModel by viewModels()
    private lateinit var textViews: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Vincular los TextView del 0 al 36 de forma directa
        textViews = listOf(
            view.findViewById(R.id.tv0), view.findViewById(R.id.tv1), view.findViewById(R.id.tv2), view.findViewById(R.id.tv3),
            view.findViewById(R.id.tv4), view.findViewById(R.id.tv5), view.findViewById(R.id.tv6), view.findViewById(R.id.tv7),
            view.findViewById(R.id.tv8), view.findViewById(R.id.tv9), view.findViewById(R.id.tv10), view.findViewById(R.id.tv11),
            view.findViewById(R.id.tv12), view.findViewById(R.id.tv13), view.findViewById(R.id.tv14), view.findViewById(R.id.tv15),
            view.findViewById(R.id.tv16), view.findViewById(R.id.tv17), view.findViewById(R.id.tv18), view.findViewById(R.id.tv19),
            view.findViewById(R.id.tv20), view.findViewById(R.id.tv21), view.findViewById(R.id.tv22), view.findViewById(R.id.tv23),
            view.findViewById(R.id.tv24), view.findViewById(R.id.tv25), view.findViewById(R.id.tv26), view.findViewById(R.id.tv27),
            view.findViewById(R.id.tv28), view.findViewById(R.id.tv29), view.findViewById(R.id.tv30), view.findViewById(R.id.tv31),
            view.findViewById(R.id.tv32), view.findViewById(R.id.tv33), view.findViewById(R.id.tv34), view.findViewById(R.id.tv35),
            view.findViewById(R.id.tv36)
        )
        // Inicializar todos en 0 y poner fondo negro, texto negro
        for (tv in textViews) {
            tv.text = "0"
            tv.setBackgroundColor(Color.BLACK)
            tv.setTextColor(Color.BLACK)
        }
        // Observar los números y actualizar la tabla
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allNumbers.collectLatest { numbers ->
                if (numbers.isEmpty()) {
                    // Si la lista está vacía, poner todos en 0 y fondo negro
                    for (tv in textViews) {
                        tv.text = "0"
                        tv.setBackgroundColor(Color.BLACK)
                        tv.setTextColor(Color.BLACK)
                    }
                } else {
                    val counts = IntArray(37) { 0 }
                    numbers.forEach { if (it.number in 0..36) counts[it.number]++ }
                    for (i in 0..36) {
                        textViews[i].text = counts[i].toString()
                        setTableCellColor(textViews[i], counts[i])
                    }
                }
            }
        }
    }

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
        fun newInstance(): TableFragment {
            return TableFragment()
        }
    }
} 