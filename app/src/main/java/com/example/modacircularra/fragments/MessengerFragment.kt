package com.example.modacircularra.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.modacircularra.adapters.UserAdapter
import com.example.modacircularra.classes.Usuario
import com.example.modacircularra.databinding.FragmentMessengerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager

class MessengerFragment : Fragment() {

    private var binding: FragmentMessengerBinding? = null
    private lateinit var userList: ArrayList<Usuario>
    private lateinit var adapter: UserAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessengerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initializations
        userList = ArrayList()
        adapter = UserAdapter(requireContext(), userList)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding?.userRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        binding?.userRecyclerView?.adapter = adapter

        // Get list of messages
        userList.clear()
        val users = HashSet<String>()

        val userID = auth.currentUser?.uid
        if (userID != null) {
            val messageDB = db.collection("Mensajes")
            messageDB.whereEqualTo("remitente", userID).get().addOnSuccessListener { result ->
                for (document in result) {
                    val recUserID = document.getString("destinatario")
                    if (recUserID != null && users.add(recUserID)) {
                        val userDB = db.collection("Usuarios")
                        userDB.whereEqualTo("id", recUserID).get().addOnSuccessListener { result ->
                            for (document in result) {
                                val user = document.toObject(Usuario::class.java)
                                userList.add(user)
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            messageDB.whereEqualTo("destinatario", userID)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val senderUserID = document.getString("remitente")
                        if (senderUserID != null && users.add(senderUserID)) {
                            val userDB = db.collection("Usuarios")
                            userDB.whereEqualTo("id", senderUserID).get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        val user = document.toObject(Usuario::class.java)
                                        userList.add(user)
                                    }
                                    adapter.notifyDataSetChanged()
                                }
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}