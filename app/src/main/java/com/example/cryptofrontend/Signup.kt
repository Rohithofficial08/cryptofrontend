package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Signup : AppCompatActivity() {

    private var walletAddress: String? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Get references to all required UI elements
        val connectButton = findViewById<LinearLayout>(R.id.connectWalletButtonLayout)
        val registerButton = findViewById<LinearLayout>(R.id.rrrregis)
        val loginText = findViewById<TextView>(R.id.r6kdcs2d66u7)

        val usernameField = findViewById<EditText>(R.id.rnzmyrya1j6k)
        val emailField = findViewById<EditText>(R.id.roixfpfnopu)
        val passwordField = findViewById<EditText>(R.id.editTextPasswordSignup)

        // Connect wallet button logic
        connectButton.setOnClickListener {
            walletAddress = "0x1234567890abcdef1234567890abcdef12345678"
            val url = "http://192.168.137.1:3000/getBalance/$walletAddress"

            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@Signup, "Failed to connect to wallet API", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(this@Signup, "API Error", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val jsonData = response.body!!.string()
                            val jsonObject = JSONObject(jsonData)
                            val balance = jsonObject.getString("balance")

                            runOnUiThread {
                                Toast.makeText(
                                    this@Signup,
                                    "Wallet: $walletAddress\nBalance: $balance",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            })
        }

        // Register button logic
        registerButton.setOnClickListener {
            val username = usernameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Replace with real registration logic
                Toast.makeText(
                    this,
                    "Register clicked\nUsername: $username\nEmail: $email\nPassword: $password",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Redirect to Login activity
        loginText.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}
