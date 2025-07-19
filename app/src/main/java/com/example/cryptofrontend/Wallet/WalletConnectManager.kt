package com.example.cryptofrontend.wallet

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class WalletConnectManager(
    private val context: Context,
    private val onSuccess: (String, String, String) -> Unit,
    private val onError: (String) -> Unit,
    private val onTransactionSuccess: (String) -> Unit = {},
    private val onBalanceUpdate: (String) -> Unit = {},
    private val onTransactionReceived: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    private val http = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingAddress = ""
    private var pendingNonce = ""
    private val TAG = "WalletConnectManager"
    private val API_BASE_URL = "http://192.168.0.32:5000/api"

    fun connectWallet() {
        Log.d(TAG, "🚀 Starting MetaMask connection...")

        // Step 1: Ask for wallet address first
        showWalletAddressDialog()
    }

    private fun showWalletAddressDialog() {
        val input = EditText(context)
        input.hint = "0x1234567890abcdef..."

        AlertDialog.Builder(context)
            .setTitle("🦊 Connect MetaMask Wallet")
            .setMessage("Please enter your MetaMask wallet address:")
            .setView(input)
            .setPositiveButton("Get Nonce") { _, _ ->
                val address = input.text.toString().trim()
                if (isValidAddress(address)) {
                    pendingAddress = address
                    fetchNonce(address)
                } else {
                    Toast.makeText(context, "Invalid wallet address format", Toast.LENGTH_SHORT).show()
                    showWalletAddressDialog()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                onError("Wallet connection cancelled")
            }
            .setCancelable(false)
            .show()
    }

    private fun isValidAddress(address: String): Boolean {
        return address.startsWith("0x") && address.length == 42
    }

    private fun fetchNonce(address: String) {
        Log.d(TAG, "🔢 Fetching nonce for: $address")

        val url = "$API_BASE_URL/metamask/nonce"
        val body = """{"address":"$address"}""".toRequestBody("application/json".toMediaTypeOrNull())

        http.post(url, body,
            onFail = { msg ->
                Log.e(TAG, "❌ Nonce failed: $msg")
                onError("Nonce request failed: $msg")
            }
        ) { json ->
            try {
                val nonce = JSONObject(json).getString("nonce")
                pendingNonce = nonce
                Log.d(TAG, "✅ Nonce received: $nonce")

                // Step 2: Show nonce and ask user to sign
                showNonceAndSigningDialog(nonce)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Invalid nonce: ${e.message}")
                onError("Invalid nonce response: ${e.message}")
            }
        }
    }

    private fun showNonceAndSigningDialog(nonce: String) {
        // Create a custom layout for the dialog
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        // Nonce display
        val nonceLabel = TextView(context).apply {
            text = "📝 Nonce to Sign:"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val nonceText = TextView(context).apply {
            text = nonce
            textSize = 14f
            setBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"))
            setPadding(20, 20, 20, 20)
            setTextIsSelectable(true)
        }

        // Signature input
        val sigLabel = TextView(context).apply {
            text = "\n✍ Paste Signature Here:"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val signatureInput = EditText(context).apply {
            hint = "0x..."
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            maxLines = 5
        }

        layout.addView(nonceLabel)
        layout.addView(nonceText)
        layout.addView(sigLabel)
        layout.addView(signatureInput)

        AlertDialog.Builder(context)
            .setTitle("🦊 Sign Message in MetaMask")
            .setMessage("1. Copy the nonce above\n2. Open MetaMask and sign it\n3. Paste the signature below")
            .setView(layout)
            .setPositiveButton("Open MetaMask") { _, _ ->
                // Open MetaMask app
                openMetaMaskApp()

                // After opening MetaMask, show signature input dialog
                handler.postDelayed({
                    showSignatureInputDialog(nonce)
                }, 2000)
            }
            .setNeutralButton("I have Signature") { _, _ ->
                val signature = signatureInput.text.toString().trim()
                if (isValidSignature(signature)) {
                    Log.d(TAG, "✅ Signature received: ${signature.take(20)}...")
                    onSuccess(pendingAddress, signature, "success")
                } else {
                    Toast.makeText(context, "Invalid signature format", Toast.LENGTH_SHORT).show()
                    showSignatureInputDialog(nonce)
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                onError("Signing cancelled")
            }
            .setCancelable(false)
            .show()
    }

    private fun openMetaMaskApp() {
        try {
            // Try to open MetaMask app
            val metamaskIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("metamask://")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (metamaskIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(metamaskIntent)
                Log.d(TAG, "✅ Opened MetaMask app")
                Toast.makeText(context, "🦊 Opening MetaMask...", Toast.LENGTH_SHORT).show()
            } else {
                // Fallback: open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://metamask.app.link/")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
                Log.d(TAG, "✅ Opened MetaMask in browser")
                Toast.makeText(context, "🌐 Opening MetaMask in browser...", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to open MetaMask: ${e.message}")
            Toast.makeText(context, "⚠ Please open MetaMask manually", Toast.LENGTH_LONG).show()
        }
    }

    private fun showSignatureInputDialog(nonce: String) {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val instructionText = TextView(context).apply {
            text = "📱 EASY Steps to Sign (No Coding!):\n\n" +
                    "1. Open MetaMask app\n" +
                    "2. Tap 'Browser' tab (bottom)\n" +
                    "3. Go to: myetherwallet.com\n" +
                    "4. Tap 'Access My Wallet'\n" +
                    "5. Select 'MetaMask'\n" +
                    "6. Tap 'Message' → 'Sign Message'\n" +
                    "7. Paste the nonce below in the message box\n" +
                    "8. Tap 'Sign Message'\n" +
                    "9. Sign when MetaMask asks\n" +
                    "10. Copy the signature and paste here"
            textSize = 13f
        }

        val nonceDisplay = TextView(context).apply {
            text = "📋 Copy This Nonce:\n$nonce"
            textSize = 14f
            setBackgroundColor(android.graphics.Color.parseColor("#E8F5E8"))
            setPadding(20, 15, 20, 15)
            setTextIsSelectable(true)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val signatureInput = EditText(context).apply {
            hint = "Paste signature here (starts with 0x...)"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 4
            maxLines = 6
        }

        layout.addView(instructionText)
        layout.addView(TextView(context).apply { text = "\n" })
        layout.addView(nonceDisplay)
        layout.addView(TextView(context).apply { text = "\n" })
        layout.addView(signatureInput)

        AlertDialog.Builder(context)
            .setTitle("✍ Get Signature from MetaMask")
            .setView(layout)
            .setPositiveButton("Submit Signature") { _, _ ->
                val signature = signatureInput.text.toString().trim()
                if (isValidSignature(signature)) {
                    Log.d(TAG, "🎉 Valid signature received!")
                    Toast.makeText(context, "✅ Signature verified! Creating account...", Toast.LENGTH_SHORT).show()
                    onSuccess(pendingAddress, signature, "success")
                } else {
                    Toast.makeText(context, "❌ Invalid signature format. Please try again.", Toast.LENGTH_LONG).show()
                    showSignatureInputDialog(nonce)
                }
            }
            .setNeutralButton("Open MEW") { _, _ ->
                // Open MyEtherWallet directly
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myetherwallet.com"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    Toast.makeText(context, "🌐 Opening MyEtherWallet...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Please open MetaMask browser manually", Toast.LENGTH_SHORT).show()
                }
                showSignatureInputDialog(nonce)
            }
            .setNegativeButton("Cancel") { _, _ ->
                onError("Signing cancelled")
            }
            .setCancelable(false)
            .show()
    }

    private fun isValidSignature(signature: String): Boolean {
        return signature.startsWith("0x") && signature.length > 100
    }

    // Other methods...
    fun sendAPTWithNote(toAddress: String, amount: String, note: String) {
        Toast.makeText(context, "Send APT feature will be implemented later", Toast.LENGTH_SHORT).show()
    }

    fun refreshBalance() {
        // To be implemented
    }

    fun getCurrentWalletAddress(): String = pendingAddress

    fun cleanup() {
        // Nothing to cleanup in this version
    }

    private fun OkHttpClient.post(
        url: String,
        body: RequestBody,
        onFail: (String) -> Unit,
        onOk: (String) -> Unit
    ) {
        val req = Request.Builder().url(url).post(body).build()
        newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.post { onFail("Network error: ${e.message}") }
            }

            override fun onResponse(call: Call, response: Response) {
                handler.post {
                    if (response.isSuccessful) {
                        onOk(response.body?.string() ?: "")
                    } else {
                        onFail("HTTP ${response.code}: ${response.message}")
                    }
                }
            }
        })
    }
}