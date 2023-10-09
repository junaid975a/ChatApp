package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

class EditProfile : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userPhNo: EditText
    private lateinit var userEmail: EditText
    private lateinit var saveProfileButton: Button

    private lateinit var reference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        userName = findViewById(R.id.user_name_txt)
        userPhNo = findViewById(R.id.user_phNo_txt)
        userEmail = findViewById(R.id.user_email_txt)
        saveProfileButton = findViewById(R.id.save_profile_btn)


    }
}