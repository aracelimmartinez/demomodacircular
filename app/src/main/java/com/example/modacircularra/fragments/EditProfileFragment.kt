package com.example.modacircularra.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.modacircularra.R
import com.example.modacircularra.databinding.FragmentEditProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.regex.Pattern

class EditProfileFragment : Fragment() {

    private var binding: FragmentEditProfileBinding? = null
    private var imageUri: Uri? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                binding?.profilePic?.setImageURI(uri)
                uploadImageToStorage(uri)
            } else {
                Toast.makeText(context, "No se seleccionó una imágen.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialization
        val newPic: ImageView = binding!!.profilePic
        val newName: EditText = binding!!.name
        val newLastName: EditText = binding!!.lastname
        val newEmail: EditText = binding!!.email
        val newPsw: EditText = binding!!.changePsw
        val confirmPsw: EditText = binding!!.confirmPsw
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getUserData()

        // Set click listener for updating profile picture
        binding?.editPic?.setOnClickListener {
            selectImageFromGallery()
        }

        // Save button
        binding?.buttonSave?.setOnClickListener {
            updateData(newName, newLastName, newEmail, newPsw, confirmPsw)
        }

        // Cancel button
        binding?.buttonCancel?.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(
                R.id.fragment_container,
                AccProfileFragment()
            )
            transaction.commit()
        }
    }

    private fun selectImageFromGallery() {
        selectImageLauncher.launch("image/*")
    }

    private fun getUserData() {
        val user = auth.currentUser
        user?.let {
            db.collection("Usuarios").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombre = document.getString("nombre") ?: ""
                        val apellido = document.getString("apellido") ?: ""
                        val email = document.getString("email") ?: ""
                        val foto = document.getString("foto") ?: ""

                        binding?.name?.setText(nombre)
                        binding?.lastname?.setText(apellido)
                        binding?.email?.setText(email)

                        if (foto.isNotEmpty()) {
                            Glide.with(this).load(foto).into(binding!!.profilePic)
                        }
                    }
                }
        }
    }

    private fun uploadImageToStorage(imageUri: Uri) {
        val user = auth.currentUser
        user?.let {
            val storageRef = storage.reference.child("profile_pics/${user.uid}.jpg")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUriToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al subir la imágen.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageUriToFirestore(imageUri: String) {
        val user = auth.currentUser
        user?.let {
            val userUpdates = hashMapOf(
                "foto" to imageUri
            )
            db.collection("Usuarios").document(user.uid)
                .update(userUpdates as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(context, "Imágen guardada correctamente.", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error al guardar la imágen en Firestore.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun updateData(
        newName: EditText,
        newLastName: EditText,
        newEmail: EditText,
        newPsw: EditText,
        confirmPsw: EditText
    ) {
        val user = auth.currentUser
        val newNameText = newName.text.toString().trim()
        val newLastNameText = newLastName.text.toString().trim()
        val newEmailText = newEmail.text.toString().trim()
        val newPswText = newPsw.text.toString().trim()
        val confirmPswText = confirmPsw.text.toString().trim()

        val passwordPattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,12}$")
        val passwordMatcher = passwordPattern.matcher(newPswText)

        user?.let { currentUser ->
            db.collection("Usuarios").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val currentName = document.getString("nombre") ?: ""
                        val currentLastName = document.getString("apellido") ?: ""
                        val currentEmail = document.getString("email") ?: ""

                        // Data change validation
                        val isNameChanged = newNameText != currentName
                        val isLastNameChanged = newLastNameText != currentLastName
                        val isEmailChanged = newEmailText != currentEmail
                        val isPasswordChanged = newPswText.isNotEmpty()

                        if (!isNameChanged && !isLastNameChanged && !isEmailChanged && !isPasswordChanged) {
                            Snackbar.make(
                                requireView(),
                                "Debe modificar para guardar cambios.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            return@addOnSuccessListener
                        }

                        // Data validation
                        if (newNameText.isEmpty() || newLastNameText.isEmpty() || newEmailText.isEmpty()) {
                            Snackbar.make(
                                requireView(),
                                "Por favor, completa todos los campos.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            return@addOnSuccessListener
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmailText)
                                .matches()
                        ) {
                            Snackbar.make(
                                requireView(),
                                "Introduce un correo electrónico válido.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            return@addOnSuccessListener
                        } else if (isPasswordChanged) {
                            if (!passwordMatcher.matches()) {
                                Snackbar.make(
                                    requireView(),
                                    "La contraseña debe tener entre 8 y 12 caracteres, incluir una mayúscula, un número y un símbolo especial.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                return@addOnSuccessListener
                            } else if (newPswText != confirmPswText) {
                                Snackbar.make(
                                    requireView(),
                                    "Las contraseñas no coinciden.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                return@addOnSuccessListener
                            }
                        }

                        val userUpdates = hashMapOf<String, Any>()
                        if (isNameChanged) userUpdates["nombre"] = newNameText
                        if (isLastNameChanged) userUpdates["apellido"] = newLastNameText
                        if (isEmailChanged) userUpdates["email"] = newEmailText

                        // Update firebase
                        db.collection("Usuarios").document(currentUser.uid)
                            .update(userUpdates)
                            .addOnSuccessListener {
                                // Update email
                                if (isEmailChanged) {
                                    currentUser.updateEmail(newEmailText)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Snackbar.make(
                                                    requireView(),
                                                    "Correo electrónico actualizado.",
                                                    Snackbar.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }

                                // Update psw
                                if (isPasswordChanged) {
                                    currentUser.updatePassword(newPswText)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Snackbar.make(
                                                    requireView(),
                                                    "Contraseña actualizada.",
                                                    Snackbar.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }

                                Snackbar.make(
                                    requireView(),
                                    "Cambios guardados correctamente.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                val transaction = parentFragmentManager.beginTransaction()
                                transaction.replace(
                                    R.id.fragment_container,
                                    AccProfileFragment()
                                )
                                transaction.commit()
                            }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
