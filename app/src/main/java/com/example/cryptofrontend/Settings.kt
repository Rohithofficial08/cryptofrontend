package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Get user session (replace with your actual keys if different!)
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = sharedPref.getString("USER_ID", "User")      // Or "USERNAME" if you save that
        val walletAddress = sharedPref.getString("WALLET", "0x...")

        // Set wallet name (row with profile icon)
        val nameLabel = findViewById<TextView>(R.id.rfvcxujvv5c)
        nameLabel.text = username ?: "User"

        // Set wallet address (USER ID row)
        val userIdLabel = findViewById<TextView>(R.id.rrx6lnk18ar)
        userIdLabel.text = walletAddress ?: "ID not found"

        // Go back on arrow click
        findViewById<ImageView>(R.id.roteehk58t3l)?.setOnClickListener {
            finish() // Return to Home or previous screen
        }

        // Log out icon and text (either triggers logout)
        val logoutIcon = findViewById<ImageView>(R.id.logoutIcon)
        val logoutText = findViewById<TextView>(R.id.logoutText)
        val doLogout = {
            sharedPref.edit().clear().apply()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login::class.java))
            finishAffinity()
        }
        logoutIcon.setOnClickListener { doLogout() }
        logoutText.setOnClickListener { doLogout() }
    }
}
