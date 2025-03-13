package com.example.modacircularra.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.modacircularra.databinding.ActivityPayBinding

class PayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set character limits
        binding.num.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
        binding.cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
        binding.date.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(5))

        // Set click listener for the Pay button
        binding.btnPay.setOnClickListener {
            if (validateFields()) {
                Toast.makeText(this, "Compra exitosa", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateFields(): Boolean {
        return binding.name.text.isNotEmpty() &&
                binding.num.text.isNotEmpty() &&
                binding.date.text.isNotEmpty() &&
                binding.cvv.text.isNotEmpty()
    }

}
