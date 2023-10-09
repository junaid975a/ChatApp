package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {

    private lateinit var input_name: EditText
    private lateinit var input_email: EditText
    private lateinit var input_password: EditText
    private lateinit var signup_btn: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
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
            val name = input_name.text.toString()

            mDbRef = FirebaseDatabase.getInstance().getReference("User")
            val flag = intArrayOf(0)
            mDbRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        if (user != null && user.email == email) {
                            flag[0] = 1
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            if (flag[0] == 1) {
                Toast.makeText(this@SignUp, "User already exists", Toast.LENGTH_SHORT).show()
            }
            else
                signUp(name,email,password);

        }
    }

    private fun signUp(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener{ verificationTask ->
                        if(verificationTask.isSuccessful) {
                            Toast.makeText(this@SignUp, "User Registered Successfully, Please check your email", Toast.LENGTH_LONG).show()

                            val firebaseUser = mAuth.currentUser
                            val user_id = firebaseUser?.uid

                            if(user_id != null) {
                                val databaseReference = FirebaseDatabase.getInstance().getReference("User").child(user_id)
                                val hashMap = hashMapOf(
                                    "uid" to user_id,
                                    "name" to name,
                                    "email" to email,
                                    "phoneNo" to "",
                                    "imageUrl" to "default"
                                )

                                databaseReference.setValue(hashMap).addOnCompleteListener { task1 ->
                                    if(task1.isSuccessful) {
                                        mAuth.signOut()
                                        startActivity(Intent(this@SignUp, Login::class.java))
                                    }
                                }
                            }
                        } else{
                            Toast.makeText(this@SignUp, verificationTask.exception?.message,  Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this@SignUp, task.exception?.message,  Toast.LENGTH_LONG).show()
                }
            }
    }

//    private fun addUserToDatabase(name: String, email: String, uid: String) {
//        mDbRef = FirebaseDatabase.getInstance().getReference()
//        mDbRef.child("User").child(uid).setValue(User(name,email,uid))
//    }
}