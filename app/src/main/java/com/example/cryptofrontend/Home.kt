package com.example.cryptofrontend

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val token = intent.getStringExtra("TOKEN")
        val userId = intent.getStringExtra("USER_ID")
        val wallet = intent.getStringExtra("WALLET")

        val welcomeText = findViewById<TextView>(R.id.textWelcome)
        val tokenText = findViewById<TextView>(R.id.textToken)

        val logoutButton = findViewById<Button>(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }


        welcomeText.text = "Welcome, User ID: $userId\nWallet: $wallet"
        tokenText.text = "Token:\n$token"

        // ✅ Set text color to black
        welcomeText.setTextColor(Color.BLACK)
        tokenText.setTextColor(Color.BLACK)
    }


}
