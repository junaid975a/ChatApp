package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var input_name: EditText
    private lateinit var input_email: EditText
    private lateinit var input_password: EditText
    private lateinit var signup_btn: Button

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        input_name = findViewById(R.id.input_name)
        input_email = findViewById(R.id.input_email)
        input_password = findViewById(R.id.input_password)
        signup_btn = findViewById(R.id.signup_btn)

        signup_btn.setOnClickListener{
            val email = input_email.text.toString()
            val password = input_password.text.toString()

            signUp(email,password);

        }
    }

    private fun signUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    success code -> jump to home
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    startActivity(intent)
                } else {
                   Toast.makeText(this@SignUp, "Some Error Occurred", Toast.LENGTH_LONG).show()
                }
            }
    }
}