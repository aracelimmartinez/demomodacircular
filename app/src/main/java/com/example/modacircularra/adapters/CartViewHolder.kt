package com.example.modacircularra.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modacircularra.viewModel.PrendaViewModel
import com.example.modacircularra.databinding.LayoutCartBinding

class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = LayoutCartBinding.bind(view)

    fun render(prenda: PrendaViewModel) {
        binding.title.text = prenda.titulo
        binding.price.text = "$${prenda.precio}"
        Glide.with(binding.clothesPic.context).load(prenda.foto).into(binding.clothesPic)
    }

}