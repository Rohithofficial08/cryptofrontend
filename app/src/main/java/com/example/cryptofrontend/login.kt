package com.example.cryptofrontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Load images
        val image1 = findViewById<ShapeableImageView>(R.id.rwjyecesuohc)
        val image2 = findViewById<ShapeableImageView>(R.id.rlex0molg35b)

        Glide.with(this)
            .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/8uH0iELUOQ/0j5vm0fd_expires_30_days.png")
            .into(image1)

        Glide.with(this)
            .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/8uH0iELUOQ/m8524b8y_expires_30_days.png")
            .into(image2)

        val usernameField = findViewById<EditText>(R.id.r4518u5l8ffv)

        val loginButton = findViewById<Button>(R.id.r8qh6yj2f6wc)
        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            if (username.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enter your ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
