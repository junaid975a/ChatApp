package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {


    private lateinit var input_email: EditText
    private lateinit var input_password: EditText
    private lateinit var login_btn: Button
    private lateinit var signup_btn: Button

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        input_email = findViewById(R.id.input_email)
        input_password = findViewById(R.id.input_password)
        login_btn = findViewById(R.id.login_btn)
        signup_btn = findViewById(R.id.signup_btn)

        signup_btn.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        login_btn.setOnClickListener {
            val email = input_email.text.toString()
            val password = input_password.text.toString()

            login(email,password);
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    code for logging in user
                    val intent = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login, "User Doesn't Exist", Toast.LENGTH_LONG).show()
                }
            }
    }
}