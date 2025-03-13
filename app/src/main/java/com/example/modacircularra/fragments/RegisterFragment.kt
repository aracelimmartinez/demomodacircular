package com.example.modacircularra.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.modacircularra.activities.LoginActivity
import com.example.modacircularra.classes.Usuario
import com.example.modacircularra.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button click listener
        binding?.register?.setOnClickListener {
            validate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // Function to validate user input
    private fun validate() {
        val passwordPattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+.=])(?=\\S+$).{8,12}$")
        val passwordMatcher = passwordPattern.matcher(binding!!.password.text.toString())

        if (binding!!.name.text.isNotEmpty() &&
            binding!!.lastname.text.isNotEmpty() &&
            binding!!.email.text.isNotEmpty() &&
            binding!!.password.text.isNotEmpty() &&
            binding!!.repeatPassword.text.isNotEmpty()
        ) {

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding!!.email.text.toString())
                    .matches()
            ) {
                Snackbar.make(
                    requireView(),
                    "Por favor, introduce un correo electrónico válido",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (binding!!.password.text.toString() != binding!!.repeatPassword.text.toString()) {
                Snackbar.make(
                    requireView(),
                    "Las contraseñas no coinciden",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (!passwordMatcher.matches()) {
                Snackbar.make(
                    requireView(),
                    "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, un número y un símbolo especial",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                authenticator()
            }
        } else {
            Snackbar.make(
                requireView(),
                "Se ha producido un error al querer registrar el usuario. Hay campos vacíos",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    // Function to authenticate and register user
    private fun authenticator() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(
            binding!!.email.text.toString(),
            binding!!.password.text.toString()
        )
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Send verification email
                    sendVerificationEmail(auth)
                    // Save new user to database
                    addUserFirebase(auth, db)
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        Snackbar.make(
                            requireView(),
                            "El correo electrónico ya está registrado",
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Error al crear usuario: ${exception?.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    // Function to add new user to Firestore
    private fun addUserFirebase(auth: FirebaseAuth, db: FirebaseFirestore) {
        val userID = auth.currentUser!!.uid
        val user = Usuario(
            userID,
            binding!!.name.text.toString(),
            binding!!.lastname.text.toString(),
            binding!!.email.text.toString(),
            null
        )
        // Add a new document with a generated ID to the "Usuarios" collection
        db.collection("Usuarios").document(userID).set(user)
    }

    // Function to send verification email
    private fun sendVerificationEmail(auth: FirebaseAuth) {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
        }
    }
}
