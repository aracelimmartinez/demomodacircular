package com.example.modacircularra.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modacircularra.R
import com.example.modacircularra.activities.ChatActivity
import com.example.modacircularra.activities.LoginActivity
import com.example.modacircularra.adapters.PostAdapter
import com.example.modacircularra.classes.PublicacionMain
import com.example.modacircularra.databinding.FragmentAccProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccProfileFragment : Fragment(), PostAdapter.OnPostClickListener {

    private var binding: FragmentAccProfileBinding? = null
    private lateinit var postList: ArrayList<PublicacionMain>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialization
        postList = ArrayList()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val visitingUserId = arguments?.getString("userId")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val completeName = arguments?.getString("completeName")

        // Get user data
        loadData(visitingUserId ?: currentUserId)
        // Get user posts
        mainRecyclerView(visitingUserId ?: currentUserId)

        if (visitingUserId != null && visitingUserId != currentUserId) {
            binding?.buttonEditProfile?.visibility = View.GONE
            binding?.buttonLogoff?.visibility = View.GONE
            binding?.sendMsg?.visibility = View.VISIBLE
        }

        // Edit Profile
        binding?.buttonEditProfile?.setOnClickListener {
            editProfile()
        }

        // Log Off
        binding?.buttonLogoff?.setOnClickListener {
            logOff()
        }

        // Send message
        binding?.sendMsg?.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra("id", visitingUserId)
                putExtra("name", completeName)
            }
            startActivity(intent)
        }

    }

    private fun loadData(userId: String?) {
        if (userId == null) return
        db.collection("Usuarios").document(userId).get()
            .addOnSuccessListener { document ->

                if (binding == null) return@addOnSuccessListener

                val photoUrl = document.getString("foto")
                val userName = document.getString("nombre")
                val userLastName = document.getString("apellido")

                binding?.profilePic?.let { imageView ->
                    if (!photoUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(photoUrl)
                            .into(imageView)
                    }
                }

                val fullName = "$userName $userLastName"
                binding?.name?.text = fullName
            }
    }

    private fun logOff() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun editProfile() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EditProfileFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPostClick(postId: String) {
        val fragment = PostFragment.newInstance(postId)
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null).commit()
    }

    private fun mainRecyclerView(userId: String?) {
        val adapter = PostAdapter(postList, this)
        binding?.postRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.postRecyclerView?.adapter = adapter
        val loading: ProgressBar = binding!!.loading
        val profile: ConstraintLayout = binding!!.profile

        // Search user posts
        postData(adapter, userId) {
            Handler(Looper.getMainLooper()).postDelayed({
                loading.visibility = View.GONE
                profile.visibility = View.VISIBLE
            }, 2000)
        }
    }

    private fun postData(adapter: PostAdapter, userId: String?, onComplete: () -> Unit) {
        if (userId == null) return
        postList.clear()
        db.collection("Publicacion")
            .whereEqualTo(
                "usuario",
                userId
            )
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val idPost = document.id
                    val idPrenda = document.getString("prenda")
                    val titulo = document.getString("titulo")
                    val precio = document.getString("precio")
                    val fecha = document.getLong("fecha")
                    if (idPrenda != null) {
                        db.collection("Prendas").document(idPrenda).get()
                            .addOnSuccessListener { document ->
                                val fotoPrenda = document.getString("foto")
                                val post =
                                    PublicacionMain(
                                        idPost,
                                        fotoPrenda,
                                        titulo,
                                        "$$precio",
                                        fecha
                                    )
                                postList.add(post)
                                adapter.notifyDataSetChanged()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar los posts",
                    Toast.LENGTH_SHORT
                ).show()
            }
        onComplete()
    }

    companion object {
        fun newInstance(userId: String, completeName: String): AccProfileFragment {
            val fragment = AccProfileFragment()
            val args = Bundle()
            args.putString("userId", userId)
            args.putString("completeName", completeName)
            fragment.arguments = args
            return fragment
        }
    }
}