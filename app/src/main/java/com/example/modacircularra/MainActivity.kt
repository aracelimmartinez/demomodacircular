package com.example.modacircularra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clickListener = View.OnClickListener { view ->
            val intent = Intent(this, AnuncioActivity::class.java)
            startActivity(intent)
        }

        val anuncio = findViewById<ImageView>(R.id.shirt)
        val anuncio2 = findViewById<ImageView>(R.id.shirt2)
        val anuncio3 = findViewById<ImageView>(R.id.shirt3)
        val anuncio4 = findViewById<ImageView>(R.id.shirt4)

        anuncio.setOnClickListener(clickListener)
        anuncio2.setOnClickListener(clickListener)
        anuncio3.setOnClickListener(clickListener)
        anuncio4.setOnClickListener(clickListener)

    }
}