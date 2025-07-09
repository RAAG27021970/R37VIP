package com.r37vip.app

import android.content.Context
import android.util.Log
import java.util.*

class LocationHelper(private val context: Context) {
    
    fun getCountryCode(): String {
        return try {
            Log.d("LocationHelper", "=== DETECCIÓN DE PAÍS ===")
            
            // Usar el país del sistema
            val locale = Locale.getDefault()
            val countryCode = locale.country
            Log.d("LocationHelper", "Locale del sistema: ${locale.displayCountry} (${locale.country})")
            Log.d("LocationHelper", "Idioma del sistema: ${locale.language}")
            
            if (countryCode.isNotEmpty()) {
                Log.d("LocationHelper", "Usando código de país del sistema: $countryCode")
                return countryCode
            }
            
            // Fallback: usar el idioma del sistema
            val fallbackCode = Locale.getDefault().language.uppercase()
            Log.d("LocationHelper", "Usando fallback: $fallbackCode")
            return fallbackCode
            
        } catch (e: Exception) {
            Log.e("LocationHelper", "Error al detectar país", e)
            // Fallback final
            return "UNKNOWN"
        }
    }
    
    fun getCountryName(): String {
        return try {
            val countryCode = getCountryCode()
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            Log.d("LocationHelper", "Nombre del país: $countryName")
            return countryName
        } catch (e: Exception) {
            Log.e("LocationHelper", "Error al obtener nombre del país", e)
            return "Unknown"
        }
    }
} 