package com.r37vip.app.stats

import android.util.Log
import com.r37vip.app.data.RouletteNumber

object StreetDelayCalculator {
    private const val TAG = "StreetDelayCalculator"

    // Array para los números, inicializado con -1 ("a") ya que 0 es un número válido
    private val numbers = IntArray(4) { -1 }
    // Array para los atrasos de cada casillero
    private val delays = IntArray(4) { 0 }
    // Array para las progresiones, relacionado con delays
    private val progresions = IntArray(8) { 0 }
    // Array para contar cuántas veces cada posición llega a cero
    private val counters = IntArray(8) { 0 }
    private var usedPositions = 0

    /**
     * Obtiene la calle (1-12) a la que pertenece un número
     */
    private fun getStreetNumber(number: Int): Int {
        if (number == 0) return 0
        return ((number - 1) / 3) + 1
    }

    /**
     * Convierte un valor de delay a su correspondiente valor de progresión
     */
    private fun delayToProgresion(delay: Int): Int {
        return when (delay) {
            in 1..4 -> 1
            in 5..9 -> 2
            in 10..13 -> 3
            in 14..16 -> 4
            17, 18 -> 5
            19, 20 -> 6
            21, 22 -> 7
            23 -> 8
            24 -> 9
            25 -> 10
            26 -> 11
            27 -> 12
            28 -> 13
            29 -> 14
            30 -> 15
            31 -> 17
            32 -> 18
            33 -> 20
            34 -> 22
            35 -> 24
            36 -> 26
            37 -> 28
            38 -> 30
            39 -> 32
            40 -> 24
            else -> 0  // Para cualquier otro valor no especificado
        }
    }

    /**
     * Reinicia todos los atrasos a 0 y números a -1
     */
    fun resetDelays() {
        for (i in numbers.indices) {
            numbers[i] = -1  // Volvemos a "a"
            delays[i] = 0
        }
        // Resetear los contadores y progresiones también
        for (i in counters.indices) {
            counters[i] = 0
            progresions[i] = 0
        }
        usedPositions = 0
    }

    /**
     * Calcula y actualiza los atrasos para los últimos números ingresados.
     * Los atrasos se mantienen entre llamadas hasta que se llame a resetDelays()
     */
    fun calculate(numbers: List<RouletteNumber>): List<Int> {
        if (numbers.isEmpty()) return emptyList()

        val newNumber = numbers.last().number
        val newStreet = getStreetNumber(newNumber)

        // Si es el primer número
        if (numbers.size == 1) {
            this.numbers[0] = newNumber
            delays[0] = 0
            usedPositions = 1
            return List(4) { 0 }
        }

        // Primero incrementamos atrasos solo donde hay números reales
        for (i in 0 until 4) {
            if (this.numbers[i] != -1) {  // Solo incrementamos si hay número real
                delays[i]++
            }
        }

        // Si NO es cero, verificamos coincidencias de calle
        if (newNumber != 0) {
            // Verificamos coincidencias de calle ANTES de mover números
            for (i in 0 until 4) {
                if (this.numbers[i] != -1 && getStreetNumber(this.numbers[i]) == newStreet) {
                    delays[i] = 0  // Reiniciamos atraso si coincide la calle
                    counters[i]++  // Incrementamos el contador cuando el delay llega a cero
                }
            }
        }

        // Desplazamos los números
        for (i in minOf(3, usedPositions) downTo 1) {
            this.numbers[i] = this.numbers[i-1]
        }

        // Insertamos el nuevo número
        this.numbers[0] = newNumber

        // Incrementamos la cantidad de posiciones usadas
        if (usedPositions < 4) usedPositions++

        // Actualizamos las progresiones basadas en los delays
        for (i in delays.indices) {
            progresions[i] = delayToProgresion(delays[i])
        }

        return delays.take(4).toList()
    }

    /**
     * Obtiene el atraso actual de un casillero específico
     */
    fun getDelay(position: Int): Int {
        return if (position < usedPositions) delays[position] else 0
    }

    fun getNumbers(): IntArray = numbers.clone()
    
    fun getDelays(): IntArray = delays.clone()

    fun getCounters(): IntArray = counters.clone()

    fun getProgresions(): IntArray = progresions.clone()

    fun getUsedPositions(): Int = usedPositions

    fun restoreState(savedNumbers: IntArray, savedDelays: IntArray) {
        require(savedNumbers.size == numbers.size && savedDelays.size == delays.size) {
            "Arrays must match the size of internal arrays"
        }

        Log.d(TAG, "Restaurando estado - Números antes: ${numbers.contentToString()}")
        Log.d(TAG, "Restaurando estado - Atrasos antes: ${delays.contentToString()}")
        Log.d(TAG, "Restaurando estado - Posiciones usadas antes: $usedPositions")

        // Copiamos los arrays en orden inverso para mantener la consistencia con calculate()
        for (i in 0 until numbers.size) {
            numbers[i] = savedNumbers[i]
            delays[i] = savedDelays[i]
        }
        usedPositions = savedNumbers.count { it != -1 }

        Log.d(TAG, "Restaurando estado - Números después: ${numbers.contentToString()}")
        Log.d(TAG, "Restaurando estado - Atrasos después: ${delays.contentToString()}")
        Log.d(TAG, "Restaurando estado - Posiciones usadas después: $usedPositions")
    }
}