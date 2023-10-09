package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.lang.Exception


class ProfileActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var userPhNo: TextView
    private lateinit var userEmail: TextView
    private lateinit var editProfileButton: Button
    private lateinit var profilePic : ImageView

    private lateinit var reference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storageReference: StorageReference
    private var imageUrl: Uri? = null
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null


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
        storageReference = FirebaseStorage.getInstance().getReference("uploads")

        editProfileButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, EditProfile::class.java)
            startActivity(intent)
        }


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

    private fun getFileExtension(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(uri?.let { contentResolver.getType(it) })
    }

    private fun uploadImage() {
        val pd = ProgressDialog(this@ProfileActivity)
        pd.setMessage("Uploading..")
        pd.show()

        imageUrl?.let { uri ->
            val fileReference =
                storageReference.child("${System.currentTimeMillis()}.${getFileExtension(uri)}")
            uploadTask = fileReference.putFile(uri)
            (uploadTask as UploadTask).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload Failed")
                }
                fileReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val mUri = downloadUri.toString()

                    val reference =
                        FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)
                    val map = hashMapOf<String, Any>("imageUrl" to mUri)
                    reference.updateChildren(map).addOnSuccessListener {
                        pd.dismiss()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this@ProfileActivity, e.message, Toast.LENGTH_SHORT).show()
                        pd.dismiss()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(this@ProfileActivity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null) {
            imageUrl = data.data

            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(this@ProfileActivity, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }
        }
    }

}