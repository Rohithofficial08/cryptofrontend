package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Back button
        val backBtn = findViewById<ImageView>(R.id.roteehk58t3l)
        backBtn.setOnClickListener {
            finish()
        }

        // Logout via TextView
        val logoutText = findViewById<TextView>(R.id.logoutText)
        val logoutIcon = findViewById<ImageView>(R.id.logoutIcon)

        val logoutAction = {
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(this@Settings, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        logoutText.setOnClickListener { logoutAction() }
        logoutIcon.setOnClickListener { logoutAction() }
    }
}
