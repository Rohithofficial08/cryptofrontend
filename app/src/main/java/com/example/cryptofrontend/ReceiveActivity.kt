package com.example.cryptofrontend

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ReceiveActivity : AppCompatActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var walletText: TextView
    private lateinit var refreshIcon: Button
    private lateinit var backToHomeBtn: Button

    private var userId: String? = null
    private var walletAddress: String? = null

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        qrImage = findViewById(R.id.qrImageView)
        walletText = findViewById(R.id.walletAddressTextView)
        refreshIcon = findViewById(R.id.refreshQrBtn)
        backToHomeBtn = findViewById(R.id.backToHomeBtn)

        loadSession()

        if (userId == null || walletAddress == null) {
            Toast.makeText(this, "Missing session data. Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        walletText.text = "Wallet: ${walletAddress?.let { it.take(6) + "..." + it.takeLast(4) }}"

        fetchQRCode()

        refreshIcon.setOnClickListener {
            fetchQRCode()
        }

        backToHomeBtn.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }

    private fun loadSession() {
        val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)
        walletAddress = sharedPref.getString("WALLET", null)
    }

    private fun fetchQRCode() {
        if (userId.isNullOrEmpty() || walletAddress.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        val json = JSONObject().apply {
            put("userId", userId)
            put("walletAddress", walletAddress)
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://192.168.0.2:5000/api/transactions/generate-qr") // Update IP if needed
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ReceiveActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("QR_REQUEST_ERROR", e.message ?: "Unknown error")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                runOnUiThread {
                    Log.d("QR_RESPONSE", "Raw response: $responseData")

                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonResponse = JSONObject(responseData)
                            val qrBase64 = jsonResponse.getString("qrImage")

                            val base64Data = qrBase64.substringAfter("base64,", "")
                            val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            if (bitmap != null) {
                                qrImage.setImageBitmap(bitmap)
                            } else {
                                Toast.makeText(this@ReceiveActivity, "Failed to decode QR image", Toast.LENGTH_SHORT).show()
                                Log.e("QR_PARSE_ERROR", "BitmapFactory returned null")
                            }

                            val payload = jsonResponse.optJSONObject("qrPayload")
                            if (payload != null) {
                                val scannedUserId = payload.optString("userId")
                                val scannedWallet = payload.optString("walletAddress")
                                Log.d("QR_PAYLOAD", "userId: $scannedUserId | wallet: $scannedWallet")
                            }

                        } catch (e: Exception) {
                            Toast.makeText(this@ReceiveActivity, "Failed to parse QR", Toast.LENGTH_SHORT).show()
                            Log.e("QR_PARSE_ERROR", "Exception: ${e.message}")
                        }
                    } else {
                        Toast.makeText(this@ReceiveActivity, "Failed to load QR", Toast.LENGTH_SHORT).show()
                        Log.e("QR_ERROR", "Response failed: ${response.code} | $responseData")
                    }
                }
            }

        })
    }
}

