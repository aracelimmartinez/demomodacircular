package com.example.modacircularra.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modacircularra.R
import com.example.modacircularra.classes.PublicacionMain

class PostAdapter(
    private var postList: ArrayList<PublicacionMain>,
    private val onPostClickListener: OnPostClickListener
) : RecyclerView.Adapter<PostViewHolder>() {

    interface OnPostClickListener {
        fun onPostClick(postId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(
            layoutInflater.inflate(R.layout.layout_post, parent, false),
            onPostClickListener
        )
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = postList[position]
        holder.render(item)
    }

    fun updatePostList(postList: ArrayList<PublicacionMain>) {
        this.postList = postList
        notifyDataSetChanged()
    }

}