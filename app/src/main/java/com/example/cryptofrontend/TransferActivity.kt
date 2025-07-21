package com.example.cryptofrontend

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer) // design as per your UI

        val userId = intent.getStringExtra("userId")
        val walletAddress = intent.getStringExtra("walletAddress")

        // Main amount text
        val textAmount = findViewById<TextView>(R.id.textAmount)
        // By default, show $0 or your value; update as needed
        textAmount.text = "$0"

        // Set up other UI elements similarly, e.g.
        // val key1 = findViewById<TextView>(R.id.key1)
        // key1.setOnClickListener { ... }
    }
}
