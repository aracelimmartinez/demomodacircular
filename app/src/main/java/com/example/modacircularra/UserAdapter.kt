package com.example.modacircularra

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(val context: Context, val usuarioList: ArrayList<Usuario>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        //retorna la cantidad de usuarios de la lista
        return usuarioList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = usuarioList[position]
        val completeName = currentUser.nombre + " " + currentUser.apellido
        holder.name.text = completeName
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // inicializa todas las views de layout_user
        val name = itemView.findViewById<TextView>(R.id.username)
        //val photo = itemView.findViewById<ImageView>(R.id.profile_pic)
    }

}