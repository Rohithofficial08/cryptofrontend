package com.example.cryptofrontend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Ethereum info image (top right)
        val ethInfoImage = findViewById<ImageView>(R.id.imageView4)
        ethInfoImage?.let {
            Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/8uH0iELUOQ/2hkds9ix_expires_30_days.png")
                .into(it)
        }

        // Wallet info
        val walletBalanceText = findViewById<TextView>(R.id.walletBalanceText)
        val walletNameText = findViewById<TextView>(R.id.walletNameText)
        val walletAddress = intent.getStringExtra("WALLET") ?: "Unknown"
        walletNameText.text = "Ethereum"
        walletBalanceText.text = "$ 55.002"

        // Logout logic
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            val intent = Intent(this@Home, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Notification button
        val notificationIcon = findViewById<ImageView>(R.id.notificationIcon)
        notificationIcon.setOnClickListener {
            val intent = Intent(this@Home, Notification::class.java)
            startActivity(intent)
        }
        // settings button
        val settingsIcon = findViewById<ImageView>(R.id.settingsIcon)
        settingsIcon.setOnClickListener {
            val intent = Intent(this@Home, Settings::class.java)
            startActivity(intent)
        }




    }
}
