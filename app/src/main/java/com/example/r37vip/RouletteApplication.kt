package com.example.r37vip

import android.app.Application
import com.example.r37vip.data.RouletteDatabase

class RouletteApplication : Application() {
    val database: RouletteDatabase by lazy { RouletteDatabase.getDatabase(this) }
} 