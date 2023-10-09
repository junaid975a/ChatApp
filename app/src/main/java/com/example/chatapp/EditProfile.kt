package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfile : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userPhNo: EditText
    private lateinit var userEmail: EditText
    private lateinit var saveProfileButton: Button

    private lateinit var reference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        userName = findViewById(R.id.user_name_txt)
        userPhNo = findViewById(R.id.user_phNo_txt)
        userEmail = findViewById(R.id.user_email_txt)
        saveProfileButton = findViewById(R.id.save_profile_btn)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    userName.text = Editable.Factory.getInstance().newEditable(user?.name)
                    userEmail.text = Editable.Factory.getInstance().newEditable(user?.email)
                    userPhNo.text = Editable.Factory.getInstance().newEditable(user?.phoneNo)
                    userID = user?.uid.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        saveProfileButton.setOnClickListener{ view ->
            val username_str = userName.text.toString()
            val contact_str = userPhNo.text.toString()
            if (contact_str.length != 10) {
                Toast.makeText(
                    this@EditProfile,
                    "Contact number should be 10 digits",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference()

                reference.child("User").child(userID).child("name").setValue(username_str)
                // reference.child("Users").child(userID).child("email").setValue(email_str)
                reference.child("User").child(userID).child("phoneNo").setValue(contact_str)
                //reference.child("Users").child(userID).child("usertype").setValue(user_type_value)
                Toast.makeText(this@EditProfile, "Updated", Toast.LENGTH_SHORT).show()
            }
        }

    }
}