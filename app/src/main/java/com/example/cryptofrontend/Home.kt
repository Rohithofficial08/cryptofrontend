package com.example.cryptofrontend

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Home : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private val backendUrl = "http://192.168.0.2:5000"  // Your backend IP
    private val client = OkHttpClient()
    private var isWalletShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // SharedPreferences
        sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val walletAddress = sharedPref.getString("WALLET", null)

        val walletBalanceText = findViewById<TextView>(R.id.walletBalanceText)
        val walletNameText = findViewById<TextView>(R.id.walletNameText)

        // Wallet address toggle elements
        val profileWalletText = findViewById<TextView>(R.id.profileWalletText)
        val showWalletEye = findViewById<ImageView>(R.id.showWalletEye)

        // Hide address by default (••••••••••••••)
        profileWalletText.text = "••••••••••••••"
        isWalletShown = false

        // Toggle visibility
        showWalletEye.setOnClickListener {
            isWalletShown = !isWalletShown
            if (isWalletShown) {
                profileWalletText.text = walletAddress ?: "ID: Unknown"
                showWalletEye.setImageResource(R.drawable.ic_eye_off)
            } else {
                profileWalletText.text = "••••••••••••••"
                showWalletEye.setImageResource(R.drawable.ic_eye)
            }
        }

        // Fetch balance
        if (!walletAddress.isNullOrBlank()) {
            getLiveBalance(walletAddress, walletBalanceText)
        } else {
            walletBalanceText.text = "$0.00"
        }

        walletNameText.text = "Ethereum" // Example display token name

        // Logout button
        findViewById<Button>(R.id.logoutButton)?.setOnClickListener {
            sharedPref.edit().clear().apply()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // Bottom nav buttons
        findViewById<LinearLayout>(R.id.receiveBtn)?.setOnClickListener {
            startActivity(Intent(this, ReceiveActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.paymentBtn)?.setOnClickListener {
            Toast.makeText(this, "Payment screen coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.walletIcon)?.setOnClickListener {
            Toast.makeText(this, "Already on Wallet screen", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.notificationIcon)?.setOnClickListener {
            startActivity(Intent(this, Notification::class.java))
        }

        findViewById<ImageView>(R.id.settingsIcon)?.setOnClickListener {
            // ✅ Go to Settings page
            startActivity(Intent(this, Settings::class.java))
        }

        // Optional profile image (can be replaced with your own URL)
        findViewById<ImageView>(R.id.imageView4)?.let {
            Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/8uH0iELUOQ/2hkds9ix_expires_30_days.png")
                .into(it)
        }
    }

    // Fetch wallet balance from backend
    private fun getLiveBalance(wallet: String, balanceView: TextView) {
        val url = "$backendUrl/api/wallet/balance/$wallet"
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    balanceView.text = "Error"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && !data.isNullOrBlank()) {
                        try {
                            val obj = JSONObject(data)
                            val balance = obj.optString("balance", "0")
                            balanceView.text = "$balance"
                        } catch (e: Exception) {
                            balanceView.text = "Error"
                        }
                    } else {
                        balanceView.text = "Error"
                    }
                }
            }
        })
    }
}
