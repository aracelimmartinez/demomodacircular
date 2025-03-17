package com.example.modacircularra.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modacircularra.R
import com.example.modacircularra.adapters.PostAdapter
import com.example.modacircularra.classes.PublicacionMain
import com.example.modacircularra.databinding.FragmentAllPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllPostsFragment : Fragment(), PostAdapter.OnPostClickListener {

    private var binding: FragmentAllPostsBinding? = null
    private lateinit var postList: ArrayList<PublicacionMain>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var collection: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllPostsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialization
        postList = ArrayList()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        collection = arguments?.getString("collection") ?: "Publicacion"

        mainRecyclerView()
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

    private fun mainRecyclerView() {
        val adapter = PostAdapter(postList, this)
        binding?.postRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.postRecyclerView?.adapter = adapter

        //Initialization
        val loading: ProgressBar = binding!!.loading
        val posts: ConstraintLayout = binding!!.posts

        // Get posts from Firebase
        getPosts(adapter) {
            Handler(Looper.getMainLooper()).postDelayed({
                loading.visibility = View.GONE
                posts.visibility = View.VISIBLE
            }, 1000)
        }

        // SearchButton function
        searchButton(postList, adapter)
    }

    private fun getPosts(adapter: PostAdapter, onComplete: () -> Unit) {
        postList.clear()

        if (collection == "Favoritos") {
            val user = auth.currentUser
            user?.let {
                db.collection(collection)
                    .whereEqualTo("idUsuario", user.uid)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val idPost = document.getString("idPost")
                            if (idPost != null) {
                                db.collection("Publicacion").document(idPost).get()
                                    .addOnSuccessListener { doc ->
                                        val idPrenda = doc.getString("prenda")
                                        val titulo = doc.getString("titulo")
                                        val precio = doc.getString("precio")
                                        val fecha = doc.getLong("fecha")
                                        if (idPrenda != null) {
                                            db.collection("Prendas").document(idPrenda).get()
                                                .addOnSuccessListener { doc ->
                                                    val fotoPrenda = doc.getString("foto")
                                                    val post = PublicacionMain(
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
                        }
                    }
            }
        } else {
            db.collection(collection).get().addOnSuccessListener { result ->
                for (document in result) {
                    val idPost = document.id
                    val idPrenda = document.getString("prenda")
                    val titulo = document.getString("titulo")
                    val precio = document.getString("precio")
                    val fecha = document.getLong("fecha")
                    if (idPrenda != null) {
                        db.collection("Prendas").document(idPrenda).get()
                            .addOnSuccessListener { doc ->
                                val fotoPrenda = doc.getString("foto")
                                val post =
                                    PublicacionMain(idPost, fotoPrenda, titulo, "$$precio", fecha)
                                postList.add(post)
                                adapter.notifyDataSetChanged()
                            }
                    }
                }
            }
            onComplete()
        }
    }

    private fun searchButton(postList: ArrayList<PublicacionMain>, adapter: PostAdapter) {
        binding?.search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredList = postList.filter { post ->
                    post.titulo?.lowercase()?.contains(newText.lowercase()) ?: false
                }
                adapter.updatePostList(ArrayList(filteredList))
                return false
            }
        })
    }

    companion object {
        fun newInstance(collection: String): AllPostsFragment {
            val fragment = AllPostsFragment()
            val args = Bundle()
            args.putString("collection", collection)
            fragment.arguments = args
            return fragment
        }
    }
}

