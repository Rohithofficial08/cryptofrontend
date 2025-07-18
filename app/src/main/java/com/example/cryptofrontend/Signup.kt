package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class Signup : AppCompatActivity() {

    private lateinit var walletAddress: String
    private lateinit var signature: String
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val connectWalletBtn = findViewById<LinearLayout>(R.id.connectWalletButtonLayout)
        val registerButton = findViewById<LinearLayout>(R.id.rrrregis)
        val loginText = findViewById<TextView>(R.id.r6kdcs2d66u7)

        val usernameField = findViewById<EditText>(R.id.rnzmyrya1j6k)
        val emailField = findViewById<EditText>(R.id.roixfpfnopu)
        val passwordField = findViewById<EditText>(R.id.editTextPasswordSignup)

        connectWalletBtn.setOnClickListener {
            connectWalletAndGetSignature { address, sig ->
                walletAddress = address
                signature = sig
                runOnUiThread {
                    Toast.makeText(this, "Wallet Connected ✅", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerButton.setOnClickListener {
            val username = usernameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (username.isBlank() || email.isBlank() || password.isBlank() ||
                !::walletAddress.isInitialized || !::signature.isInitialized
            ) {
                Toast.makeText(this, "Fill all fields & connect wallet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val json = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("password", password)
                put("walletAddress", walletAddress)
                put("signature", signature)
            }

            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("http://192.168.137.8:3000/api/auth/signup")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@Signup, "Signup Failed ❌", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        if (response.isSuccessful) {
                            Toast.makeText(this@Signup, "Signup Successful ✅", Toast.LENGTH_SHORT).show()

                            // Save to SharedPreferences
                            getSharedPreferences("user_session", MODE_PRIVATE).edit().apply {
                                putString("userId", "roh40618")
                                putString("walletAddress", walletAddress)
                                apply()
                            }

                            val intent = Intent(this@Signup, ReceiveActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@Signup, "Signup Error ❌", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }

    private fun connectWalletAndGetSignature(callback: (String, String) -> Unit) {
        val simulatedWalletAddress = "0x1234567890abcdef1234567890abcdef12345678"
        val nonceUrl = "http://192.168.137.1:3000/api/metamask/nonce?walletAddress=$simulatedWalletAddress"

        client.newCall(Request.Builder().url(nonceUrl).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Signup, "Nonce fetch failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val nonceData = JSONObject(response.body!!.string())
                val nonce = nonceData.getString("nonce")
                val simulatedSignature = "signed($nonce)"

                val json = JSONObject().apply {
                    put("walletAddress", simulatedWalletAddress)
                    put("signature", simulatedSignature)
                }

                val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val verifyRequest = Request.Builder()
                    .url("http://192.168.137.1:3000/api/metamask/verify")
                    .post(body)
                    .build()

                client.newCall(verifyRequest).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(this@Signup, "Wallet verification failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            callback(simulatedWalletAddress, simulatedSignature)
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@Signup, "Invalid Signature", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }
        })
    }
}
