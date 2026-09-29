package com.example.payukiller

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {

            if (
                username.text.isNullOrBlank() ||
                email.text.isNullOrBlank() ||
                password.text.isNullOrBlank() ||
                confirmPassword.text.isNullOrBlank()
            ) {
                Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.text.toString() != confirmPassword.text.toString()) {
                Toast.makeText(this, "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "สมัครสมาชิกสำเร็จ (โหมดทดลอง)",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}
