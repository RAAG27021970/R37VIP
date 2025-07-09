package com.r37vip.app

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ExcelExporter {
    
    companion object {
        fun exportToExcel(context: Context, numbers: List<String>): File? {
            return try {
                Log.d("ExcelExporter", "Iniciando exportación...")
                
                // Obtener el código del país
                val locationHelper = LocationHelper(context)
                val countryCode = locationHelper.getCountryCode()
                Log.d("ExcelExporter", "Código de país: $countryCode")
                
                // Crear el nombre del archivo con fecha_hora_PAIS
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "${timestamp}_$countryCode.csv"
                Log.d("ExcelExporter", "Nombre del archivo: $fileName")
                
                // Intentar guardar en Downloads primero
                var file = trySaveToDownloads(context, fileName, numbers, locationHelper)
                
                // Si falla, guardar en el almacenamiento interno de la app
                if (file == null) {
                    Log.d("ExcelExporter", "Fallback: guardando en almacenamiento interno")
                    file = trySaveToInternalStorage(context, fileName, numbers, locationHelper)
                }
                
                if (file != null) {
                    Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.export_error), Toast.LENGTH_SHORT).show()
                }
                
                return file
                
            } catch (e: Exception) {
                Log.e("ExcelExporter", "Error durante la exportación", e)
                Toast.makeText(context, context.getString(R.string.export_error), Toast.LENGTH_SHORT).show()
                return null
            }
        }
        
        private fun trySaveToDownloads(context: Context, fileName: String, numbers: List<String>, locationHelper: LocationHelper): File? {
            return try {
                val downloadsFolderName = getDownloadsFolderName()
                Log.d("ExcelExporter", "Carpeta de descargas: $downloadsFolderName")
                
                val downloadsDir = findDownloadsDirectory(context, downloadsFolderName)
                Log.d("ExcelExporter", "Directorio encontrado: ${downloadsDir?.absolutePath}")
                
                if (downloadsDir == null) {
                    Log.e("ExcelExporter", "No se pudo encontrar la carpeta de descargas")
                    return null
                }
                
                if (!downloadsDir.exists()) {
                    val created = downloadsDir.mkdirs()
                    Log.d("ExcelExporter", "Carpeta creada: $created")
                }
                
                val file = File(downloadsDir, fileName)
                Log.d("ExcelExporter", "Archivo a crear: ${file.absolutePath}")
                
                writeCsvFile(file, numbers, locationHelper)
                Log.d("ExcelExporter", "Archivo creado exitosamente en Downloads: ${file.absolutePath}")
                file
                
            } catch (e: Exception) {
                Log.e("ExcelExporter", "Error al guardar en Downloads", e)
                null
            }
        }
        
        private fun trySaveToInternalStorage(context: Context, fileName: String, numbers: List<String>, locationHelper: LocationHelper): File? {
            return try {
                val internalDir = File(context.filesDir, "exports")
                if (!internalDir.exists()) {
                    internalDir.mkdirs()
                }
                
                val file = File(internalDir, fileName)
                Log.d("ExcelExporter", "Archivo a crear en interno: ${file.absolutePath}")
                
                writeCsvFile(file, numbers, locationHelper)
                Log.d("ExcelExporter", "Archivo creado exitosamente en interno: ${file.absolutePath}")
                file
                
            } catch (e: Exception) {
                Log.e("ExcelExporter", "Error al guardar en almacenamiento interno", e)
                null
            }
        }
        
        private fun writeCsvFile(file: File, numbers: List<String>, locationHelper: LocationHelper) {
            CSVWriter(FileWriter(file)).use { writer ->
                // Crear header con números de columna (1-35)
                val header = mutableListOf<String>()
                for (col in 1..35) {
                    header.add("Col $col")
                }
                writer.writeNext(header.toTypedArray())
                
                // Llenar la grilla con los números (18 filas x 35 columnas)
                for (row in 0 until 18) {
                    val dataRow = mutableListOf<String>()
                    for (col in 0 until 35) {
                        val index = col * 18 + row
                        if (index < numbers.size && numbers[index].isNotEmpty()) {
                            dataRow.add(numbers[index])
                        } else {
                            dataRow.add("")
                        }
                    }
                    writer.writeNext(dataRow.toTypedArray())
                }
                
                // Agregar información adicional
                writer.writeNext(arrayOf(""))
                writer.writeNext(arrayOf("Información del archivo:"))
                writer.writeNext(arrayOf("Fecha de exportación: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}"))
                writer.writeNext(arrayOf("País: ${locationHelper.getCountryName()} (${locationHelper.getCountryCode()})"))
                val totalNumbers = numbers.count { it.isNotEmpty() }
                writer.writeNext(arrayOf("Total de números registrados: $totalNumbers"))
            }
        }
        
        private fun findDownloadsDirectory(context: Context, folderName: String): File? {
            val possiblePaths = listOf(
                File(context.getExternalFilesDir(null)?.parentFile?.parentFile, folderName),
                File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "Download"),
                File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "Descargas"),
                File("/storage/emulated/0", folderName),
                File("/storage/emulated/0", "Download"),
                File("/storage/emulated/0", "Descargas")
            )
            
            for (path in possiblePaths) {
                if ((path.exists() && path.canWrite()) || (!path.exists() && path.parentFile?.canWrite() == true)) {
                    return path
                }
            }
            // Si no encuentra ninguna, retorna null
            return null
        }
        
        private fun getDownloadsFolderName(): String {
            val locale = Locale.getDefault()
            return when (locale.language) {
                "es" -> "Descargas"
                "fr" -> "Téléchargements"
                "de" -> "Downloads"
                "it" -> "Download"
                "pt" -> "Downloads"
                else -> "Downloads" // Default para inglés y otros idiomas
            }
        }
        
        fun shareExcelFile(context: Context, file: File) {
            try {
                Log.d("ExcelExporter", "Compartiendo archivo: ${file.absolutePath}")
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Historial Ruleta - ${file.name}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_file)))
                
            } catch (e: Exception) {
                Log.e("ExcelExporter", "Error al compartir archivo", e)
                Toast.makeText(context, context.getString(R.string.export_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
} 