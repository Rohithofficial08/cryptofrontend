package com.example.cryptofrontend

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject

class ReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        val sharedPref: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)
        val walletAddress = sharedPref.getString("WALLET", null)

        val addressTextView = findViewById<TextView>(R.id.receiveAddressText)
        val userIdTextView = findViewById<TextView>(R.id.receiveUserIdText)
        val qrImageView = findViewById<ImageView>(R.id.receiveQrImage)

        // Show both userId and wallet address
        userIdTextView.text = "User ID: ${userId ?: "Not found"}"
        addressTextView.text = "Wallet: ${walletAddress ?: "Not found"}"

        // Generate QR code encapsulating BOTH userId and walletAddress
        if (userId.isNullOrBlank() || walletAddress.isNullOrBlank()) {
            Toast.makeText(this, "User ID or Wallet address not found", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val qrObj = JSONObject()
            qrObj.put("userId", userId)
            qrObj.put("walletAddress", walletAddress)
            val qrContent = qrObj.toString()

            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 512, 512)
            val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            qrImageView.setImageBitmap(bmp)
        } catch (e: WriterException) {
            Toast.makeText(this, "Error generating QR", Toast.LENGTH_SHORT).show()
        }
    }
}