package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject

class Payment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Camera icon → launch QR Scanner
        val scanButton = findViewById<ImageView>(R.id.r1ys7uu1nv7r)
        scanButton.setOnClickListener {
            startQrScanner()
        }

        // You can also add back button handling here for safety
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan Wallet QR Code")
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.setCaptureActivity(ScannerActivity::class.java)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                try {
                    val qrData = JSONObject(result.contents)
                    val userId = qrData.optString("userId", "")
                    val walletAddress = qrData.optString("walletAddress", "")

                    if (userId.isNotEmpty() && walletAddress.startsWith("0x")) {
                        val intent = Intent(this, TransferActivity::class.java).apply {
                            putExtra("userId", userId)
                            putExtra("walletAddress", walletAddress)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Invalid QR content.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid QR format.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Scan cancelled.", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
