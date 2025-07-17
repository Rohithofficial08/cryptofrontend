package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // ✅ Your custom splash layout

        // ✅ Show splash screen for 2 seconds then check session
        Handler(Looper.getMainLooper()).postDelayed({
            // Retrieve session data
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            val token = sharedPref.getString("TOKEN", null)
            val userId = sharedPref.getString("USER_ID", null)
            val wallet = sharedPref.getString("WALLET", null)

            // ✅ Check if session exists and navigate accordingly
            if (!token.isNullOrEmpty() && !userId.isNullOrEmpty() && !wallet.isNullOrEmpty()) {
                val intent = Intent(this, Home::class.java).apply {
                    putExtra("TOKEN", token)
                    putExtra("USER_ID", userId)
                    putExtra("WALLET", wallet)
                }
                startActivity(intent)
            } else {
                startActivity(Intent(this, Signup::class.java))
            }

            // ✅ Finish splash activity so user can't go back to it
            finish()
        }, 2000)
    }
}
