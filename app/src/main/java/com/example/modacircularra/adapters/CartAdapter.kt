package com.example.modacircularra.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modacircularra.databinding.LayoutCartBinding
import com.example.modacircularra.viewModel.CarritoViewModel
import com.example.modacircularra.viewModel.PrendaViewModel

class CartAdapter(
    private var cartList: ArrayList<PrendaViewModel>,
    private val viewModel: CarritoViewModel
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: LayoutCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun render(prenda: PrendaViewModel) {
            binding.title.text = prenda.titulo
            binding.price.text = "$${prenda.precio}"
            Glide.with(binding.clothesPic.context).load(prenda.foto).into(binding.clothesPic)

            binding.btnTrash.setOnClickListener {
                removeItem(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutCartBinding.inflate(layoutInflater, parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartList.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.render(item)
    }

    fun updateCartList(cartList: ArrayList<PrendaViewModel>) {
        this.cartList.clear()
        this.cartList.addAll(cartList)
        notifyDataSetChanged()
    }

    private fun removeItem(position: Int) {
        val item = cartList[position]
        cartList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, cartList.size)
        viewModel.removerDelCarrito(item)
    }
}




