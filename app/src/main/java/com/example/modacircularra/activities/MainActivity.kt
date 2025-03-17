package com.example.modacircularra.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.modacircularra.R
import com.example.modacircularra.classes.MyApplication
import com.example.modacircularra.databinding.ActivityMainBinding
import com.example.modacircularra.fragments.AccProfileFragment
import com.example.modacircularra.fragments.AllPostsFragment
import com.example.modacircularra.fragments.MessengerFragment
import com.example.modacircularra.viewModel.CarritoViewModel
import com.google.firebase.auth.FirebaseAuth
import android.view.View

class MainActivity : AppCompatActivity() {

    public lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: CarritoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initializations
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        viewModel = (applicationContext as MyApplication).carritoViewModel

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // AllPostsFragment initialization
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AllPostsFragment.newInstance("Publicacion"))
                .commit()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Update cart number
        viewModel.carritoCount.observe(this, Observer { count ->
            binding.carritoBadge.text =
                count.toString()  // Suponiendo que tienes un TextView en el ícono
            binding.carritoBadge.visibility =
                if (count > 0) View.VISIBLE else View.GONE  // Solo muestra el contador si hay items
        })

        // Bottom Bar
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            AllPostsFragment.newInstance("Publicacion")
                        )
                        .commit()
                    true
                }

                R.id.new_post -> {
                    val intent = Intent(this, NewPostActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_message -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MessengerFragment())
                        .commit()
                    true
                }

                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AccProfileFragment())
                        .commit()
                    true
                }

                R.id.nav_fav -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AllPostsFragment.newInstance("Favoritos"))
                        .commit()
                    true
                }

                else -> false
            }
        }

        // Button Cart
        binding.cart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
}
