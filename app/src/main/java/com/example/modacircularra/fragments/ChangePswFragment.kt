package com.example.modacircularra.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.modacircularra.databinding.FragmentChangePswBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class ChangePswFragment : Fragment() {

    // Initializing view binding and FirebaseAuth instance
    private var binding: FragmentChangePswBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePswBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initializing FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Button click listener
        binding!!.changePsw.setOnClickListener {
            validate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // Validating the email input field
    private fun validate() {
        val emailText = binding!!.email.text.toString()
        if (emailText.isNotEmpty()) {
            changePsw(emailText)
        } else {
            Toast.makeText(
                requireContext(),
                "Por favor, introduce un correo electrónico válido",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Function to handle password change via FirebaseAuth
    private fun changePsw(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    "Te enviamos un email a $email. Por favor, verifica tu bandeja de entrada",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se pudo enviar el email. Por favor, inténtalo de nuevo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
