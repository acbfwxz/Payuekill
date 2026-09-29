package com.example.payukiller

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {

            val usernameText = username.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()

            if (
                usernameText.isBlank() ||
                emailText.isBlank() ||
                passwordText.isBlank() ||
                confirmPasswordText.isBlank()
            ) {
                Toast.makeText(
                    this,
                    "กรุณากรอกข้อมูลให้ครบ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (passwordText != confirmPasswordText) {
                Toast.makeText(
                    this,
                    "รหัสผ่านไม่ตรงกัน",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                Toast.makeText(
                    this,
                    "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            registerButton.isEnabled = false

            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener { task ->

                    registerButton.isEnabled = true

                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "สมัครสมาชิกสำเร็จ",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "สมัครสมาชิกไม่สำเร็จ: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
