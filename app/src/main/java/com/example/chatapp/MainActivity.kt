package com.example.chatapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        adapter = UserAdapter(this,userList)

        userRecyclerView = findViewById(R.id.user_recycler_view)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        mDbRef.child("User").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (snap in snapshot.children) {
                    val currentUser = snap.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged() // Notify the adapter of the data change
                Log.d("MainActivity", "Data retrieved successfully. UserList size: ${userList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors, if any
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val profileMenuItem = menu?.findItem(R.id.profile)
        val logoutMenuItem = menu?.findItem(R.id.logout)

        val textColor = Color.BLACK // Set your desired text color
        if (profileMenuItem != null) {
            setMenuItemTextColor(profileMenuItem, textColor)
        }
        if (logoutMenuItem != null) {
            setMenuItemTextColor(logoutMenuItem, textColor)
        }
        return super.onCreateOptionsMenu(menu)
    }
    private fun setMenuItemTextColor(menuItem: MenuItem, color: Int) {
        val spannableString = SpannableString(menuItem.title)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            0,
            spannableString.length,
            0
        )
        menuItem.title = spannableString
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout) {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
            return true
        } else if(item.itemId == R.id.profile) {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
            return true
        }
        return true
    }
}