package com.example.cryptofrontend

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Button

class TransferActivity : AppCompatActivity() {
    private var currentAmount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val userId = intent.getStringExtra("userId") ?: "--"
        val walletAddress = intent.getStringExtra("walletAddress") ?: "--"

        findViewById<TextView>(R.id.userIdReceiver)?.text = "User ID: $userId"
        findViewById<TextView>(R.id.walletReceiver)?.text = "Wallet: $walletAddress"
        val textAmount = findViewById<TextView>(R.id.textAmount)
        textAmount.text = "$0"

        // Keypad number handlers
        val keys = mapOf(
            R.id.key0 to "0",
            R.id.key1 to "1",
            R.id.key2 to "2",
            R.id.key3 to "3",
            R.id.key4 to "4",
            R.id.key5 to "5",
            R.id.key6 to "6",
            R.id.key7 to "7",
            R.id.key8 to "8",
            R.id.key9 to "9",
            R.id.key00 to "00"
        )

        for ((id, value) in keys) {
            findViewById<TextView>(id).setOnClickListener {
                addDigit(value)
                textAmount.text = formatAmount(currentAmount)
            }
        }

        // Handle delete (backspace)
        findViewById<ImageView>(R.id.keyDelete).setOnClickListener {
            if (currentAmount.isNotEmpty()) {
                currentAmount = currentAmount.substring(0, currentAmount.length - 1)
            }
            textAmount.text = formatAmount(currentAmount)
        }

        // Optionally, handle Transfer button click:
        findViewById<Button>(R.id.transferBtn).setOnClickListener {
            // handleTransfer(currentAmount) // Your own function!
        }

    }

    private fun addDigit(digit: String) {
        // Prevent leading zeros
        if (digit == "0" || digit == "00") {
            if (currentAmount.isEmpty()) return
        }
        currentAmount += digit
        // Prevent absurdly long entries:
        if (currentAmount.length > 10) {
            currentAmount = currentAmount.substring(0, 10)
        }
    }

    private fun formatAmount(raw: String): String {
        // Prevent empty = $0
        return if (raw.isEmpty()) "$0" else "$$raw"
    }
}
