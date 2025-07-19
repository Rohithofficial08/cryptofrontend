package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptofrontend.wallet.WalletConnectManager
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class Signup : AppCompatActivity() {

    private lateinit var walletConnectManager: WalletConnectManager
    private val backendUrl = "http://192.168.0.32:5000"
    private val TAG = "SignupActivity"

    // Using your EXACT field IDs from layout
    private var usernameEt: EditText? = null
    private var emailEt: EditText? = null
    private var passwordEt: EditText? = null
    private var connectWalletButton: LinearLayout? = null
    private var registerButton: LinearLayout? = null
    private var loginText: TextView? = null

    // Store form data temporarily during wallet connection
    private var pendingUsername = ""
    private var pendingEmail = ""
    private var pendingPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        try {
            // Initialize views with YOUR EXACT IDs
            usernameEt = findViewById(R.id.rnzmyrya1j6k)           // Username field
            emailEt = findViewById(R.id.roixfpfnopu)              // Email field
            passwordEt = findViewById(R.id.editTextPasswordSignup) // Password field
            connectWalletButton = findViewById(R.id.connectWalletButtonLayout)
            registerButton = findViewById(R.id.rrrregis)
            loginText = findViewById(R.id.r6kdcs2d66u7)

            // Check if views were found
            validateViewsExist()

            // Initialize WalletConnectManager with enhanced callbacks
            walletConnectManager = WalletConnectManager(
                context = this,
                onSuccess = { walletAddress, signature, token ->
                    Log.d(TAG, "✅ Wallet connected successfully!")
                    Log.d(TAG, "Address: $walletAddress")
                    Log.d(TAG, "Signature: ${signature.take(20)}...")

                    runOnUiThread {
                        Toast.makeText(this@Signup, "✅ Wallet connected! Creating account...", Toast.LENGTH_SHORT).show()

                        // Use stored form data and send to /register API
                        sendSignupRequest(pendingUsername, pendingEmail, pendingPassword, walletAddress, signature)
                    }
                },
                onError = { error ->
                    Log.e(TAG, "❌ Wallet connection error: $error")
                    runOnUiThread {
                        Toast.makeText(this@Signup, "❌ Wallet Error: $error", Toast.LENGTH_LONG).show()
                        // Re-enable connect button
                        connectWalletButton?.isEnabled = true
                    }
                }
            )

            // Connect Wallet button - validates form FIRST then connects wallet
            connectWalletButton?.setOnClickListener {
                if (validateForm()) {
                    // Store form data temporarily
                    pendingUsername = usernameEt?.text?.toString()?.trim() ?: ""
                    pendingEmail = emailEt?.text?.toString()?.trim() ?: ""
                    pendingPassword = passwordEt?.text?.toString() ?: ""

                    Log.d(TAG, "Form validated. Connecting wallet for user: $pendingUsername")

                    // Disable button to prevent multiple clicks
                    connectWalletButton?.isEnabled = false

                    Toast.makeText(this, "🔗 Connecting to MetaMask...", Toast.LENGTH_SHORT).show()
                    walletConnectManager.connectWallet()
                }
            }

            // Register button - direct registration without wallet
            registerButton?.setOnClickListener {
                if (validateForm()) {
                    Toast.makeText(this, "📝 Registering without wallet...", Toast.LENGTH_SHORT).show()
                    registerWithoutWallet()
                }
            }

            // Login text click
            loginText?.setOnClickListener {
                Log.d(TAG, "Navigating to Login page")
                startActivity(Intent(this, Login::class.java))
                finish()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing signup: ${e.message}", e)
            Toast.makeText(this, "Error initializing signup: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateViewsExist() {
        when {
            usernameEt == null -> {
                Toast.makeText(this, "Username field not found", Toast.LENGTH_LONG).show()
                return
            }
            emailEt == null -> {
                Toast.makeText(this, "Email field not found", Toast.LENGTH_LONG).show()
                return
            }
            passwordEt == null -> {
                Toast.makeText(this, "Password field not found", Toast.LENGTH_LONG).show()
                return
            }
            connectWalletButton == null -> {
                Toast.makeText(this, "Connect wallet button not found", Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    private fun validateForm(): Boolean {
        val username = usernameEt?.text?.toString()?.trim() ?: ""
        val email = emailEt?.text?.toString()?.trim() ?: ""
        val password = passwordEt?.text?.toString() ?: ""

        return when {
            username.isEmpty() -> {
                usernameEt?.error = "Username is required"
                usernameEt?.requestFocus()
                Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show()
                false
            }
            username.length < 3 -> {
                usernameEt?.error = "Username must be at least 3 characters"
                usernameEt?.requestFocus()
                Toast.makeText(this, "Username too short", Toast.LENGTH_SHORT).show()
                false
            }
            email.isEmpty() -> {
                emailEt?.error = "Email is required"
                emailEt?.requestFocus()
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                false
            }
            !isValidEmail(email) -> {
                emailEt?.error = "Please enter a valid email"
                emailEt?.requestFocus()
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() -> {
                passwordEt?.error = "Password is required"
                passwordEt?.requestFocus()
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                passwordEt?.error = "Password must be at least 6 characters"
                passwordEt?.requestFocus()
                Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                // Clear any previous errors
                usernameEt?.error = null
                emailEt?.error = null
                passwordEt?.error = null
                true
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Register WITHOUT wallet (using /api/auth/register endpoint)
    private fun registerWithoutWallet() {
        val username = usernameEt?.text?.toString()?.trim() ?: ""
        val email = emailEt?.text?.toString()?.trim() ?: ""
        val password = passwordEt?.text?.toString() ?: ""

        Log.d(TAG, "Registering user without wallet: $username")

        val client = OkHttpClient()

        try {
            val requestBodyJson = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("password", password)
                put("walletAddress", "")  // Empty for non-wallet registration
                put("signature", "")      // Empty for non-wallet registration
            }.toString()

            val requestBody = requestBodyJson.toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("$backendUrl/api/auth/register")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Registration without wallet failed: ${e.message}")
                    runOnUiThread {
                        Toast.makeText(this@Signup, "Registration Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Registration response: $responseBody")

                    runOnUiThread {
                        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                            try {
                                val json = JSONObject(responseBody)
                                val backendToken = json.getString("token")
                                val userId = json.optString("userId", "")
                                val walletAddress = json.optString("walletAddress", "")

                                // Store all user data
                                val sharedPref = getSharedPreferences("APP_PREF", MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("JWT_TOKEN", backendToken)
                                    putString("USER_ID", userId)
                                    putString("WALLET_ADDRESS", walletAddress)
                                    putString("USERNAME", username)
                                    putString("EMAIL", email)
                                    apply()
                                }

                                Toast.makeText(this@Signup, "✅ Registration Successful! Welcome $username", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "Registration successful, navigating to Home")

                                // ✅ REDIRECT TO HOME PAGE
                                startActivity(Intent(this@Signup, Home::class.java))
                                finish()
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing registration response: ${e.message}")
                                Toast.makeText(this@Signup, "Registration failed: Invalid response", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            try {
                                val errorJson = JSONObject(responseBody ?: "{}")
                                val errorMsg = errorJson.optString("error", "Registration failed")
                                Toast.makeText(this@Signup, errorMsg, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(this@Signup, "Registration failed: $responseBody", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error creating registration request: ${e.message}")
            Toast.makeText(this, "Error creating request: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Register WITH wallet (using /api/auth/register endpoint) - SENDS SIGNATURE DIRECTLY
    private fun sendSignupRequest(
        username: String,
        email: String,
        password: String,
        walletAddress: String,
        signature: String
    ) {
        Log.d(TAG, "🚀 Sending signup request with wallet for user: $username")
        Log.d(TAG, "📍 Wallet address: $walletAddress")
        Log.d(TAG, "✍ Signature: ${signature.take(20)}...")

        val client = OkHttpClient()

        try {
            val requestBodyJson = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("password", password)
                put("walletAddress", walletAddress)
                put("signature", signature)  // ✅ SIGNATURE SENT DIRECTLY TO /register
            }.toString()

            val requestBody = requestBodyJson.toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("$backendUrl/api/auth/register")  // ✅ DIRECT TO /register (NOT /verify)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "❌ Signup with wallet failed: ${e.message}")
                    runOnUiThread {
                        connectWalletButton?.isEnabled = true // Re-enable button
                        Toast.makeText(this@Signup, "❌ Signup Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "📥 Signup with wallet response: $responseBody")

                    runOnUiThread {
                        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                            try {
                                val json = JSONObject(responseBody)
                                val backendToken = json.getString("token")
                                val userId = json.getString("userId")
                                val userWalletAddress = json.getString("walletAddress")

                                // Store all user data
                                val sharedPref = getSharedPreferences("APP_PREF", MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("JWT_TOKEN", backendToken)
                                    putString("USER_ID", userId)
                                    putString("WALLET_ADDRESS", userWalletAddress)
                                    putString("USERNAME", username)
                                    putString("EMAIL", email)
                                    apply()
                                }

                                Toast.makeText(this@Signup, "🎉 Signup Successful with Wallet! Welcome $username", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "✅ Signup with wallet successful, navigating to Home")

                                // ✅ REDIRECT TO HOME PAGE ON SUCCESS
                                startActivity(Intent(this@Signup, Home::class.java))
                                finish()

                            } catch (e: Exception) {
                                Log.e(TAG, "❌ Error parsing signup response: ${e.message}")
                                connectWalletButton?.isEnabled = true // Re-enable button
                                Toast.makeText(this@Signup, "❌ Signup failed: Invalid response", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            connectWalletButton?.isEnabled = true // Re-enable button
                            try {
                                val errorJson = JSONObject(responseBody ?: "{}")
                                val errorMsg = errorJson.optString("error", "Signup failed")
                                Toast.makeText(this@Signup, "❌ $errorMsg", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(this@Signup, "❌ Signup failed: $responseBody", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creating signup request: ${e.message}")
            runOnUiThread {
                connectWalletButton?.isEnabled = true // Re-enable button
                Toast.makeText(this, "❌ Error creating request: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::walletConnectManager.isInitialized) {
                walletConnectManager.cleanup()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-enable connect wallet button when returning from MetaMask
        connectWalletButton?.isEnabled = true
    }
}