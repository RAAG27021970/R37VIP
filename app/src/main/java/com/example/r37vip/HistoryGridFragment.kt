package com.example.r37vip

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.r37vip.data.RouletteNumber
import com.example.r37vip.viewmodels.RouletteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryGridFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GridAdapter
    private val viewModel: RouletteViewModel by viewModels()
    private var currentGridData: List<String> = List(630) { "" }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            exportToExcel()
        } else {
            Toast.makeText(context, "Permisos de ubicación necesarios para el nombre del archivo", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.historyGrid)
        
        // Set up the grid with 18 rows
        val layoutManager = GridLayoutManager(context, 18, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        
        // Initialize adapter with empty data
        adapter = GridAdapter(List(630) { "" })
        recyclerView.adapter = adapter

        // Set up delete buttons
        view.findViewById<MaterialButton>(R.id.btnDeleteLast).setOnClickListener {
            viewModel.deleteLastNumber()
        }

        view.findViewById<MaterialButton>(R.id.btnDeleteAll).setOnClickListener {
            viewModel.deleteAll()
        }

        // Set up export button
        view.findViewById<MaterialButton>(R.id.btnExport).setOnClickListener {
            checkLocationPermissionAndExport()
        }

        // Observe database changes
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allNumbers.collectLatest { numbers ->
                updateGridWithNumbers(numbers)
            }
        }
    }

    private fun checkLocationPermissionAndExport() {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            exportToExcel()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun updateGridWithNumbers(numbers: List<RouletteNumber>) {
        val gridData = MutableList(630) { "" }
        
        // Fill the grid with numbers - column by column, top to bottom
        numbers.forEachIndexed { index, rouletteNumber ->
            if (index < 630) {
                val column = index / 18  // Integer division for column number
                val row = index % 18     // Remainder for row position within column
                val position = (column * 18) + row  // Calculate position in the grid
                gridData[position] = rouletteNumber.number.toString()
            }
        }

        currentGridData = gridData
        adapter.updateData(gridData)
    }

    private fun exportToExcel() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val file = ExcelExporter.exportToExcel(requireContext(), currentGridData)
                    withContext(Dispatchers.Main) {
                        if (file != null) {
                            ExcelExporter.shareExcelFile(requireContext(), file)
                            Toast.makeText(context, "Archivo exportado a Downloads: ${file.name}", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Error al exportar el archivo. Revisa los logs.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private class GridAdapter(private var numbers: List<String>) : 
            RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.grid_cell_item, parent, false)
            return GridViewHolder(view as TextView)
        }

        override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
            holder.textView.text = numbers[position]
        }

        override fun getItemCount() = numbers.size

        fun updateData(newNumbers: List<String>) {
            numbers = newNumbers
            notifyDataSetChanged()
        }

        class GridViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }

    companion object {
        fun newInstance() = HistoryGridFragment()
    }
} 