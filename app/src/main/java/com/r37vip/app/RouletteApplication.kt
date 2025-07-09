package com.r37vip.app

import android.app.Application
import com.r37vip.app.data.RouletteDatabase

class RouletteApplication : Application() {
    val database: RouletteDatabase by lazy { RouletteDatabase.getDatabase(this) }
} 