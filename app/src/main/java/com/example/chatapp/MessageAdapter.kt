package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVED = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType==1) {
//            inflate recieved
            val view: View = LayoutInflater.from(context).inflate(R.layout.received, parent, false)
            return receivedViewHolder(view);
        }
        else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return sentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass == sentViewHolder::class.java) {
//            to do sent msg
            val viewHolder = holder as sentViewHolder
            holder.sentMessage.text = currentMessage.message
        }
        else{
//            to do receive msg
            val viewHolder = holder as receivedViewHolder
            holder.receivedMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        }
        else {
            return ITEM_RECEIVED
        }
    }

    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.text_sent_message)
    }


    class receivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessage = itemView.findViewById<TextView>(R.id.text_received_message)
    }
}