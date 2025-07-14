package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class Login : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Check if user is already logged in
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)
        val userId = sharedPref.getString("USER_ID", null)
        val wallet = sharedPref.getString("WALLET", null)

        if (token != null && userId != null && wallet != null) {
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("TOKEN", token)
                putString("USER_ID", userId)
                putString("WALLET", wallet)
                apply()
            }

            val intent = Intent(this@Login, Home::class.java)
            intent.putExtra("TOKEN", token)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("WALLET", wallet)
            startActivity(intent)

            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // ✅ Allow cleartext network policy for development
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Load images using Glide
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

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
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

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url("http://192.168.0.2:5000/api/auth/login")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Login, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(responseData ?: "")
                        val token = jsonResponse.getString("token")
                        val userId = jsonResponse.getString("userId")
                        val walletAddress = jsonResponse.getString("walletAddress")

                        // ✅ Save login session
                        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("TOKEN", token)
                            putString("USER_ID", userId)
                            putString("WALLET", walletAddress)
                            apply()
                        }

                        Toast.makeText(this@Login, "Login Successful", Toast.LENGTH_SHORT).show()

                        // ✅ Redirect to Home activity
                        val intent = Intent(this@Login, Home::class.java)
                        intent.putExtra("TOKEN", token)
                        intent.putExtra("USER_ID", userId)
                        intent.putExtra("WALLET", walletAddress)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = JSONObject(responseData ?: "{}")
                            .optString("error", "Login Failed")
                        Toast.makeText(this@Login, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
