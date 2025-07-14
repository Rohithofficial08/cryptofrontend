package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // ✅ Show logo

        // ✅ Delay for 2 seconds before checking login state
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            val token = sharedPref.getString("TOKEN", null)
            val userId = sharedPref.getString("USER_ID", null)
            val wallet = sharedPref.getString("WALLET", null)

            if (token != null && userId != null && wallet != null) {
                val intent = Intent(this, Home::class.java)
                intent.putExtra("TOKEN", token)
                intent.putExtra("USER_ID", userId)
                intent.putExtra("WALLET", wallet)
                startActivity(intent)
            } else {
                startActivity(Intent(this, Signup::class.java))
            }

            finish()
        }, 2000) // 2-second splash delay
    }
}
