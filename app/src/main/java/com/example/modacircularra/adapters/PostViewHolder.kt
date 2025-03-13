package com.example.modacircularra.adapters


import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modacircularra.classes.PublicacionMain
import com.example.modacircularra.databinding.LayoutPostBinding
import com.example.modacircularra.adapters.PostAdapter.OnPostClickListener

class PostViewHolder(view: View, private val onPostClickListener: OnPostClickListener) :
    RecyclerView.ViewHolder(view) {

    private val binding = LayoutPostBinding.bind(view)

    fun render(post: PublicacionMain) {
        binding.title.text = post.titulo
        binding.price.text = post.precio
        Glide.with(binding.clothesPic.context).load(post.foto).into(binding.clothesPic)

        itemView.setOnClickListener {
            val postId = post.id
            Log.d("PostViewHolder", "ID de la publicación: $postId")
            onPostClickListener.onPostClick(postId ?: "default")
        }
    }
}

