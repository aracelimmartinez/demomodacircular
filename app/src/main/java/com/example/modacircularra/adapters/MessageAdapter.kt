package com.example.modacircularra.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.modacircularra.classes.Mensaje
import com.example.modacircularra.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messagesList: ArrayList<Mensaje>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val item_received = 1;
    val item_sent = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            // Show received msg
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.layout_received, parent, false)
            return ReceivedViewHolder(view)
        } else {
            // Show sent msg
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.layout_sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messagesList[position]

        if (holder.javaClass == SentViewHolder::class.java) {
            // Sent msg
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.texto
        } else {
            // Received msg
            val viewHolder = holder as ReceivedViewHolder
            holder.receivedMessage.text = currentMessage.texto
        }

    }

    override fun getItemViewType(position: Int): Int {

        // Differentiate sent messages from received messages
        val currentMessage = messagesList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.remitente)) {
            item_sent
        } else {
            item_received
        }
    }


    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.sent_message)

    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessage = itemView.findViewById<TextView>(R.id.received_message)

    }

}