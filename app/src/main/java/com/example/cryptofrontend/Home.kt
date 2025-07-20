package com.example.cryptofrontend

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class Home : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ✅ Access shared preferences
        sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)

        // ✅ Logout Button
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            sharedPref.edit().clear().apply()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // ✅ Receive Button (was causing crash before — FIXED)
        val receiveBtn = findViewById<LinearLayout>(R.id.receiveBtn)
        receiveBtn.setOnClickListener {
            startActivity(Intent(this, ReceiveActivity::class.java))
        }

        // ✅ Payment Button
        val paymentBtn = findViewById<LinearLayout>(R.id.paymentBtn)
        paymentBtn.setOnClickListener {
            Toast.makeText(this, "Payment screen coming soon!", Toast.LENGTH_SHORT).show()
        }

        // ✅ Bottom nav icons
        val walletIcon = findViewById<ImageView>(R.id.walletIcon)
        val notificationIcon = findViewById<ImageView>(R.id.notificationIcon)
        val settingsIcon = findViewById<ImageView>(R.id.settingsIcon)

        walletIcon.setOnClickListener {
            Toast.makeText(this, "Already on Wallet screen", Toast.LENGTH_SHORT).show()
        }

        notificationIcon.setOnClickListener {
            startActivity(Intent(this, Notification::class.java))
        }

        settingsIcon.setOnClickListener {
            Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show()
        }

        // ✅ Show wallet data from SharedPreferences
        val walletBalanceText = findViewById<TextView>(R.id.walletBalanceText)
        val walletNameText = findViewById<TextView>(R.id.walletNameText)

        val walletBalance = sharedPref.getString("walletBalance", "$0.00")
        val walletName = sharedPref.getString("walletName", "Ethereum")

        walletBalanceText.text = walletBalance
        walletNameText.text = walletName
    }
}
