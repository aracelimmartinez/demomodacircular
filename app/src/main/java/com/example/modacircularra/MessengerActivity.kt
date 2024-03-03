package com.example.modacircularra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MessengerActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Usuario>
    private lateinit var adapter: UserAdapter
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "MessengerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        userRecyclerView = findViewById(R.id.user_recyclerView)
        db = FirebaseFirestore.getInstance()

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        userList.clear()
        val userDB = db.collection("Usuarios")
        userDB.get().addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(Usuario::class.java)
                    Log.d(TAG, "Usuario: $user")
                    userList.add(user)
                }
                adapter.notifyDataSetChanged()
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo documentos: ", exception)
            }

    }
}