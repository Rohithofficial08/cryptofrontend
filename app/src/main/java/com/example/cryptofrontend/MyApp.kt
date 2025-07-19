package com.example.cryptofrontend

import android.app.Application
import android.util.Log

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("CryptoApp", "Application initialized successfully")
    }
}