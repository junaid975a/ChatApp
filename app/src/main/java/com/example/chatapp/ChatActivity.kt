package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var chat_recycler_view: RecyclerView
    private lateinit var msg_box: EditText
    private lateinit var send_btn: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiver_uid = intent.getStringExtra("uid")

        val sender_uid = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = receiver_uid + sender_uid
        receiverRoom = sender_uid + receiver_uid

        supportActionBar?.title = name

        chat_recycler_view = findViewById(R.id.chat_recycler_view)
        msg_box = findViewById(R.id.msg_box)
        send_btn = findViewById(R.id.send_btn)
        mDbRef = FirebaseDatabase.getInstance().getReference()

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chat_recycler_view.layoutManager = LinearLayoutManager(this)
        chat_recycler_view.adapter = messageAdapter

//        logic for adding data to recycler view
        mDbRef.child("Chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(snap in snapshot.children) {
                        val message = snap.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        send_btn.setOnClickListener{
//            adding the msgs to database
            val message = msg_box.text.toString()
            val message_object = Message(message,sender_uid)
            mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                .setValue(message_object).addOnSuccessListener {
                    mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                        .setValue(message_object)
                }
            msg_box.setText("")
        }


    }
}