package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide


class ProfileActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var userPhNo: TextView
    private lateinit var userEmail: TextView
    private lateinit var editProfileButton: Button
    private lateinit var profilePic : ImageView

    private lateinit var reference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storageReference: StorageReference

    companion object {
        const val IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userName = findViewById(R.id.user_name)
        userPhNo = findViewById(R.id.user_phNo)
        userEmail = findViewById(R.id.user_email)
        editProfileButton = findViewById(R.id.edit_profile_btn)
        profilePic = findViewById(R.id.profile_pic)

        editProfileButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, EditProfile::class.java)
            startActivity(intent)
        }

        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)

        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    userName.text = user?.name
                    userEmail.text = user?.email
                    userPhNo.text = user?.phoneNo
                    if(user?.imageUrl == "default") {
                        profilePic.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        Glide.with(this@ProfileActivity).load(user?.imageUrl).into(profilePic)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        profilePic.setOnClickListener{ openImage() }
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }
}