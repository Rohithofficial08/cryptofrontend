package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class Signup : AppCompatActivity() {

    private val backendUrl = "http://192.168.0.2:5000"
    private var usernameEt: EditText? = null
    private var emailEt: EditText? = null
    private var passwordEt: EditText? = null
    private var connectWalletButton: LinearLayout? = null
    private var registerButton: LinearLayout? = null
    private var loginText: TextView? = null
    private var walletAddress: String = ""

    // Extended timeout OkHttpClient for long-running registration (airdrop/mint)
    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)   // Adjust as needed for your backend
        .writeTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        usernameEt = findViewById(R.id.rnzmyrya1j6k)
        emailEt = findViewById(R.id.roixfpfnopu)
        passwordEt = findViewById(R.id.editTextPasswordSignup)
        connectWalletButton = findViewById(R.id.connectWalletButtonLayout)
        registerButton = findViewById(R.id.rrrregis)
        loginText = findViewById(R.id.r6kdcs2d66u7)

        connectWalletButton?.setOnClickListener { showWalletAddressDialog() }

        registerButton?.setOnClickListener {
            if (validateForm()) {
                sendSignupRequest(
                    usernameEt?.text?.toString()?.trim() ?: "",
                    emailEt?.text?.toString()?.trim() ?: "",
                    passwordEt?.text?.toString() ?: "",
                    walletAddress
                )
            }
        }

        loginText?.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun showWalletAddressDialog() {
        val input = EditText(this)
        input.hint = "0x..."

        AlertDialog.Builder(this)
            .setTitle("Connect MetaMask Wallet")
            .setMessage("Enter your MetaMask wallet address:")
            .setView(input)
            .setPositiveButton("Connect") { _, _ ->
                val address = input.text.toString().trim()
                if (address.startsWith("0x") && address.length == 42) {
                    walletAddress = address
                    Toast.makeText(this, "MetaMask connected!\nAddress: $walletAddress", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Invalid wallet address", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun validateForm(): Boolean {
        val username = usernameEt?.text?.toString()?.trim() ?: ""
        val email = emailEt?.text?.toString()?.trim() ?: ""
        val password = passwordEt?.text?.toString() ?: ""

        return when {
            username.isEmpty() -> {
                usernameEt?.error = "Username is required"; false
            }
            email.isEmpty() -> {
                emailEt?.error = "Email is required"; false
            }
            password.isEmpty() -> {
                passwordEt?.error = "Password is required"; false
            }
            walletAddress.isBlank() -> {
                Toast.makeText(this, "Connect your wallet first", Toast.LENGTH_SHORT).show(); false
            }
            else -> true
        }
    }

    private fun sendSignupRequest(
        username: String,
        email: String,
        password: String,
        walletAddress: String?
    ) {
        // Optionally show a loading spinner here

        val json = JSONObject().apply {
            put("username", username)
            put("email", email)
            put("password", password)
            put("walletAddress", walletAddress ?: "")
        }
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("$backendUrl/api/auth/register")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@Signup,
                        "Registration Failed: ${e.localizedMessage ?: e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && body != null) {
                        try {
                            val jsonResponse = JSONObject(body)
                            val token = jsonResponse.optString("token", "")
                            val userId = jsonResponse.optString("userId", "")
                            val walletAddr = jsonResponse.optString("walletAddress", walletAddress ?: "")

                            // Store credentials for Home/login auto-login
                            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("TOKEN", token)
                                putString("USER_ID", userId)
                                putString("WALLET", walletAddr)
                                apply()
                            }
                            Toast.makeText(this@Signup, "Registration successful!", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@Signup, Home::class.java))
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(this@Signup, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // For known errors, prompt to login
                        if (body?.contains("User already exists") == true) {
                            AlertDialog.Builder(this@Signup)
                                .setTitle("Account Exists")
                                .setMessage("User already exists. Would you like to log in instead?")
                                .setPositiveButton("Login") { _, _ ->
                                    startActivity(Intent(this@Signup, Login::class.java))
                                    finish()
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        } else {
                            Toast.makeText(this@Signup, "Error: $body", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }
}
