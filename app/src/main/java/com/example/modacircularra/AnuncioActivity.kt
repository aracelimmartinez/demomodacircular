package com.example.modacircularra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class AnuncioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anuncio)

        val btn_addCart = findViewById<Button>(R.id.button_cart)
        val btn_ar = findViewById<Button>(R.id.button_tryme)
        val btn_cart = findViewById<ImageView>(R.id.cart)

        btn_addCart.setOnClickListener {
            //addToCart()
        }

        btn_ar.setOnClickListener {
            funAR()
        }

        btn_cart.setOnClickListener {
            //funCart()
        }

    }

    private fun funCart() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun funAR() {
        val intent = Intent(this, RAActivity::class.java)
        startActivity(intent)
    }

    private fun addToCart() {

    }
}