package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class Login : AppCompatActivity() {

    // Use the correct backend IP/port
    private val backendUrl = "http://192.168.137.1:5000"

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auto-login if session exists
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)
        val userId = sharedPref.getString("USER_ID", null)
        val wallet = sharedPref.getString("WALLET", null)

        if (token != null && userId != null && wallet != null) {
            val intent = Intent(this@Login, Home::class.java).apply {
                putExtra("TOKEN", token)
                putExtra("USER_ID", userId)
                putExtra("WALLET", wallet)
            }
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Allow cleartext HTTP on older Android versions
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Load UI images
        val image1 = findViewById<ShapeableImageView>(R.id.rwjyecesuohc)
        val image2 = findViewById<ShapeableImageView>(R.id.rlex0molg35b)

        Glide.with(this)
            .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/8uH0iELUOQ/0j5vm0fd_expires_30_days.png")
            .into(image1)
        Glide.with(this)
            .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/8uH0iELUOQ/m8524b8y_expires_30_days.png")
            .into(image2)

        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.r8qh6yj2f6wc)

        // --- REGISTER TEXT --- add this block
        val registerText = findViewById<TextView>(R.id.rsm7o7ulp1a) // Set to your Register TextView ID

        registerText?.setOnClickListener {
            // Go to the Signup activity when register text clicked
            startActivity(Intent(this@Login, Signup::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        val jsonBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("$backendUrl/api/auth/login")
            .post(jsonBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Login, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful) {
                        try {
                            val jsonResponse = JSONObject(responseData ?: "")
                            val token = jsonResponse.optString("token", "")
                            val userId = jsonResponse.optString("userId", "")
                            val walletAddress = jsonResponse.optString("walletAddress", "")

                            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("TOKEN", token)
                                putString("USER_ID", userId)
                                putString("WALLET", walletAddress)
                                apply()
                            }

                            Toast.makeText(this@Login, "Login Successful", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@Login, Home::class.java).apply {
                                putExtra("TOKEN", token)
                                putExtra("USER_ID", userId)
                                putExtra("WALLET", walletAddress)
                            }
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(this@Login, "Login failed: Invalid response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val error = try {
                            JSONObject(responseData ?: "{}").optString("error", "Login Failed")
                        } catch (e: Exception) {
                            responseData ?: "Login Failed"
                        }
                        Toast.makeText(this@Login, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
