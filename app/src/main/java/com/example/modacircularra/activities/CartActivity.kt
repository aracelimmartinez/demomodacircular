package com.example.modacircularra.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modacircularra.adapters.CartAdapter
import com.example.modacircularra.databinding.ActivityCartBinding
import com.example.modacircularra.viewModel.CarritoViewModel
import com.example.modacircularra.classes.MyApplication
import com.example.modacircularra.viewModel.PrendaViewModel

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var viewModel: CarritoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the application and the ViewModel instance
        viewModel = (applicationContext as MyApplication).carritoViewModel

        // Initialize the adapter with an empty list and the ViewModel
        adapter = CartAdapter(ArrayList(), viewModel)

        // Set up the RecyclerView with a linear layout manager and the adapter
        binding.cartListRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.cartListRecyclerview.adapter = adapter

        // Observe the cart list from the ViewModel and update the adapter when it changes
        viewModel.carrito.observe(this) { cartList ->
            adapter.updateCartList(cartList as ArrayList<PrendaViewModel>)
        }

        // Observe the total price from the ViewModel and update the text view when it changes
        viewModel.totalPrecio.observe(this) { total ->
            binding.price.text = "$$total"
        }

        // Set click listener for the add more button to navigate to the MainActivity
        binding.addMore.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the shop button to show the payment method alert
        binding.shop.setOnClickListener {
            showPaymentMethodAlert()
        }
    }

    // Show an alert dialog asking the user if they want to pay by transfer
    private fun showPaymentMethodAlert() {
        AlertDialog.Builder(this)
            .setTitle("Método de pago")
            .setMessage("Si usted desea pagar con transferencia, comuniquece con el vendedor.")
            .setPositiveButton("Aceptar") { dialog, which ->
                // Navigate to PayActivity when the user accepts
                val intent = Intent(this, PayActivity::class.java)
                startActivity(intent)
            }
            .create()
            .show()
    }
}


