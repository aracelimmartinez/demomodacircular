package com.example.modacircularra.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modacircularra.activities.ChatActivity
import com.example.modacircularra.R
import com.example.modacircularra.classes.Usuario

class UserAdapter(val context: Context, val usuarioList: ArrayList<Usuario>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        // Returns number of users in the list
        return usuarioList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = usuarioList[position]
        val completeName = currentUser.nombre + " " + currentUser.apellido
        holder.name.text = completeName

        // Load profile pic
        Glide.with(context)
            .load(currentUser.foto) // Suponiendo que currentUser.foto es la URL de la imagen
            .placeholder(R.drawable.profile)
            .into(holder.photo)

        // Start the chat when clicked, send the full name and the ID of the receiver user
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", completeName)
            intent.putExtra("id", currentUser.id)
            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize all the views in layout_user
        val name = itemView.findViewById<TextView>(R.id.username)
        val photo: ImageView = itemView.findViewById(R.id.profile_pic)
    }

}