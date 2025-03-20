package com.example.modacircularra.fragments

import android.app.AlertDialog
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
import com.bumptech.glide.Glide
import com.example.modacircularra.R
import com.example.modacircularra.activities.ChatActivity
import com.example.modacircularra.activities.MainActivity
import com.example.modacircularra.activities.RAActivity
import com.example.modacircularra.classes.MyApplication
import com.example.modacircularra.viewModel.CarritoViewModel
import com.example.modacircularra.databinding.FragmentPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostFragment : Fragment() {

    private var binding: FragmentPostBinding? = null
    private lateinit var postId: String
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: CarritoViewModel
    private var isFav: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("postId").orEmpty()
        viewModel = (requireActivity().applicationContext as MyApplication).carritoViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initializations
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val loading: ProgressBar = binding!!.loading
        val post: ConstraintLayout = binding!!.post

        // Get post data
        getPostData(postId, db) {
            Handler(Looper.getMainLooper()).postDelayed({
                loading.visibility = View.GONE
                post.visibility = View.VISIBLE
            }, 1000)
        }

        // Function "Probarme"
        binding?.buttonTryme?.setOnClickListener {
            val intent = Intent(activity, RAActivity::class.java)
            startActivity(intent)
        }

        // Add post to cart
        binding?.buttonAddCart?.setOnClickListener {
            addToCart(postId, db)
        }

        // Add to fav
        binding?.fav?.setOnClickListener {
            if (isFav) {
                removeFromFav(postId, db, auth)
            } else {
                addToFav(postId, db, auth)
            }
        }

        // Report button
        binding?.report?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Reportar publicación")
                .setMessage("¿Desea reportar esta publicación?")
                .setPositiveButton("Sí") { _, _ ->
                    Toast.makeText(
                        requireContext(),
                        "Gracias por tu reporte. Lo revisaremos.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Function delete post
        binding?.trash?.setOnClickListener {
            deletePost(postId)
        }
    }

    private fun deletePost(postId: String) {

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar publicación")
            .setMessage("¿Estás seguro de que deseas eliminar esta publicación?")
            .setPositiveButton("Sí") { _, _ ->
                // Initialization
                val storage = FirebaseStorage.getInstance()
                val db = FirebaseFirestore.getInstance()

                db.collection("Publicacion").document(postId).get()
                    .addOnSuccessListener { document ->
                        val clothesId = document.getString("prenda")
                        if (clothesId != null) {
                            // Obtener la foto de la prenda
                            db.collection("Prendas").document(clothesId).get()
                                .addOnSuccessListener { prendaDoc ->
                                    val fotoUrl = prendaDoc.getString("foto")
                                    if (!fotoUrl.isNullOrEmpty()) {
                                        val fotoRef = storage.getReferenceFromUrl(fotoUrl)
                                        fotoRef.delete()
                                    }
                                    db.collection("Prendas").document(clothesId).delete()
                                }
                        }
                        db.collection("Publicacion").document(postId).delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Publicación eliminada",
                                    Toast.LENGTH_SHORT
                                ).show()
                                (activity as MainActivity).binding.bottomNavigation.menu.findItem(R.id.nav_home).isChecked =
                                    true
                                (activity as MainActivity).supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.fragment_container,
                                        AllPostsFragment.newInstance("Publicacion")
                                    )
                                    .commit()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Error al eliminar la publicación",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Remove from favorites
    private fun removeFromFav(post: String, db: FirebaseFirestore, auth: FirebaseAuth) {
        val user = auth.currentUser
        val userId = user?.uid
        if (userId != null) {
            db.collection("Favoritos")
                .whereEqualTo("idPost", post)
                .whereEqualTo("idUsuario", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            db.collection("Favoritos").document(document.id).delete()
                                .addOnSuccessListener {
                                    binding?.fav?.setImageResource(R.drawable.heart)
                                    isFav = false
                                }
                        }
                    }
                }
        }
    }

    private fun addToFav(post: String, db: FirebaseFirestore, auth: FirebaseAuth) {
        val user = auth.currentUser
        val userId = user?.uid
        if (userId != null) {
            db.collection("Favoritos")
                .whereEqualTo("idPost", post)
                .whereEqualTo("idUsuario", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val newDocRef = db.collection("Favoritos").document()
                        val favData = hashMapOf(
                            "id" to newDocRef.id,
                            "idPost" to post,
                            "idUsuario" to userId
                        )
                        newDocRef.set(favData).addOnSuccessListener {
                            binding?.fav?.setImageResource(R.drawable.red_heart)
                            isFav = true
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun addToCart(post: String, db: FirebaseFirestore) {
        db.collection("Publicacion").document(post).get().addOnSuccessListener { document ->
            val clothesId = document.getString("prenda")
            val title = document.getString("titulo")
            val price = document.getString("precio")

            if (clothesId != null) {
                db.collection("Prendas").document(clothesId).get()
                    .addOnSuccessListener { document ->
                        val pic = document.getString("foto")
                        viewModel.agregarAlCarrito(post, pic, title, price)

                        val prenda = viewModel.obtenerUltimaPrenda()
                        if (prenda != null) {
                            val dialogFragment = CarritoDialogFragment.newInstance(prenda)
                            dialogFragment.show(
                                requireActivity().supportFragmentManager,
                                "CarritoDialogFragment"
                            )
                        }
                    }
            }
        }
    }

    private fun getPostData(post: String, db: FirebaseFirestore, onComplete: () -> Unit) {

        db.collection("Publicacion").document(post).get().addOnSuccessListener { document ->
            val userId = document.getString("usuario")
            val clothesId = document.getString("prenda")
            val description = document.getString("descripcion")
            val postStatus = document.getString("estado")
            val price = document.getString("precio")
            val idPost = document.getString("id")
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == currentUserId) {
                binding?.trash?.visibility = View.VISIBLE
                binding?.msg?.visibility = View.GONE
            } else {
                binding?.trash?.visibility = View.GONE
                binding?.msg?.visibility = View.VISIBLE
            }

            binding?.text?.text = description
            binding?.priceEdit?.text = "$$price"

            if (postStatus == "Disponible") {
                binding?.status?.text = "En venta"
            } else {
                binding?.status?.text = "No disponible"
            }

            if (userId != null && clothesId != null) {
                db.collection("Usuarios").document(userId).get()
                    .addOnSuccessListener { document ->
                        val name = document.getString("nombre")
                        val lastName = document.getString("apellido")
                        val completeName = "$name $lastName"
                        val profilePic = document.getString("foto")

                        binding?.username?.text = completeName

                        if (!profilePic.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profilePic)
                                .into(binding!!.profilePic)
                        }

                        // Send message
                        binding?.msg?.setOnClickListener {
                            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                                putExtra("id", userId)
                                putExtra("name", completeName)
                            }
                            startActivity(intent)
                        }

                        // Profile
                        binding?.username?.setOnClickListener {
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                            if (currentUserId == userId) {
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, AccProfileFragment())
                                    .addToBackStack(null)
                                    .commit()
                                (requireActivity() as MainActivity).binding.bottomNavigation.menu.findItem(
                                    R.id.nav_profile
                                ).isChecked = true
                            } else {
                                val fragment = AccProfileFragment.newInstance(userId, completeName)
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit()
                                (requireActivity() as MainActivity).binding.bottomNavigation.menu.findItem(
                                    R.id.nav_profile
                                ).isChecked = true
                            }
                        }
                    }
                db.collection("Prendas").document(clothesId).get()
                    .addOnSuccessListener { document ->
                        val size = document.getString("talle")
                        val colors = document.getString("colores")
                        val material = document.getString("material")
                        val model = document.getString("modelo")
                        val clothesStatus = document.getString("estado")
                        val pic = document.getString("foto")

                        binding?.sizeEdit?.text = size
                        binding?.colorsEdit?.text = colors
                        binding?.materialEdit?.text = material
                        binding?.modelEdit?.text = model
                        binding?.clothesStatusEdit?.text = clothesStatus

                        Glide.with(this)
                            .load(pic)
                            .into(binding!!.clothesPic)
                    }

                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                db.collection("Favoritos")
                    .whereEqualTo("idUsuario", currentUserId)
                    .whereEqualTo("idPost", idPost)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty()) {
                            binding?.fav?.setImageResource(R.drawable.red_heart)
                            isFav = true
                        } else {
                            binding?.fav?.setImageResource(R.drawable.heart)
                            isFav = false
                        }
                    }
            }
            onComplete()
        }
    }

    companion object {
        fun newInstance(postId: String): PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putString("postId", postId)
            fragment.arguments = args
            return fragment
        }
    }
}
