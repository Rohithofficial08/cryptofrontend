package com.example.cryptofrontend

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val token = intent.getStringExtra("TOKEN")
        val userId = intent.getStringExtra("USER_ID")
        val wallet = intent.getStringExtra("WALLET")

        val welcomeText = findViewById<TextView>(R.id.textWelcome)
        val tokenText = findViewById<TextView>(R.id.textToken)

        welcomeText.text = "Welcome, User ID: $userId\nWallet: $wallet"
        tokenText.text = "Token:\n$token"
    }
}
