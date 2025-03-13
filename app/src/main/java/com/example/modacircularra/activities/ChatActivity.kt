package com.example.modacircularra.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modacircularra.adapters.MessageAdapter
import com.example.modacircularra.classes.Mensaje
import com.example.modacircularra.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentChange

class ChatActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityChatBinding

    // Adapter for displaying messages
    private lateinit var messageAdapter: MessageAdapter

    // List of messages
    private lateinit var messagesList: ArrayList<Mensaje>

    // Firebase Firestore instance
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the sender's and receiver's IDs
        val name = intent.getStringExtra("name")
        val receiverId = intent.getStringExtra("id")
        val senderId = FirebaseAuth.getInstance().currentUser?.uid

        // Set the ActionBar title to the receiver's name
        supportActionBar?.title = name

        // Initialize message list and adapter
        messagesList = ArrayList()
        messageAdapter = MessageAdapter(this, messagesList)

        // Setup RecyclerView
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Load messages from Firestore
        loadMessages(senderId, receiverId)

        // Send message on button click
        binding.send.setOnClickListener {
            addMessage(binding.messageBox.text.toString(), senderId, receiverId)
        }
    }

    // Function to load messages from Firestore
    private fun loadMessages(senderId: String?, receiverId: String?) {
        messagesList.clear()
        db.collection("Mensajes")
            .whereIn("remitente", listOf(senderId, receiverId))
            .whereIn("destinatario", listOf(senderId, receiverId))
            .orderBy("fecha")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (docChange in snapshots!!.documentChanges) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        val message = docChange.document.toObject(Mensaje::class.java)
                        messagesList.add(message)
                        //messageAdapter.notifyDataSetChanged() // Refresh the adapter
                        messageAdapter.notifyItemInserted(messagesList.size - 1) // Refresh only the new item
                    }
                }
            }
    }

    // Function to add a new message to Firestore
    private fun addMessage(text: String, sender: String?, receiver: String?) {
        db = FirebaseFirestore.getInstance()
        val message = Mensaje(null, text, sender, receiver, System.currentTimeMillis())
        db.collection("Mensajes").add(message).addOnSuccessListener { documentReference ->
            // Get the document ID and assign it to the message object
            message.id = documentReference.id
            // Update the document with the new ID
            db.collection("Mensajes").document(documentReference.id).set(message)
            binding.messageBox.text.clear()
        }
    }
}

