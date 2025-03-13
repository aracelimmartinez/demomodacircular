package com.example.modacircularra.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.modacircularra.R
import com.example.modacircularra.classes.Prenda
import com.example.modacircularra.classes.Publicacion
import com.example.modacircularra.databinding.ActivityNewPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class NewPostActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityNewPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var imageUri: Uri
    private lateinit var idPrendaGlobal: String

    // For picking media
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // User selects an image
                binding.image.setImageURI(uri)
                imageUri = uri
            } else {
                // User does not select an image, return to the main screen
                Toast.makeText(this, "No se seleccionó una imágen.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Launch media picker
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        // Spinner definition
        val items = arrayOf("Usado", "Poco uso", "Sin uso")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        binding.estado.adapter = adapter

        // Next button action
        binding.btnNext.setOnClickListener {
            validateFirstForm()
        }

        // Post button action
        binding.btnPost.setOnClickListener {
            validateSecondForm()
        }
    }

    // Validate the first form
    private fun validateFirstForm() {
        if (binding.talle.text.isNotEmpty() && binding.colores.text.isNotEmpty() &&
            binding.material.text.isNotEmpty() && binding.modelo.text.isNotEmpty()
        ) {
            addPrendaFirebase()
            switchToSecondForm()
        } else {
            showError()
        }
    }

    // Switch to the second form
    private fun switchToSecondForm() {
        binding.text.visibility = View.GONE
        binding.estado.visibility = View.GONE
        binding.talle.visibility = View.GONE
        binding.colores.visibility = View.GONE
        binding.material.visibility = View.GONE
        binding.modelo.visibility = View.GONE
        binding.btnNext.visibility = View.GONE
        binding.titulo.visibility = View.VISIBLE
        binding.descripcion.visibility = View.VISIBLE
        binding.precio.visibility = View.VISIBLE
        binding.btnPost.visibility = View.VISIBLE
    }

    // Validate the second form
    private fun validateSecondForm() {
        if (binding.titulo.text.isNotEmpty() && binding.descripcion.text.isNotEmpty() && binding.precio.text.isNotEmpty()) {
            addPost()
        } else {
            showError()
        }
    }

    // Function to add a new post to Firestore
    private fun addPost() {
        val idUser = auth.currentUser?.uid
        val post = Publicacion(
            null,
            idUser,
            idPrendaGlobal,
            binding.titulo.text.toString(),
            binding.descripcion.text.toString(),
            "Disponible",
            binding.precio.text.toString(),
            System.currentTimeMillis()
        )
        db.collection("Publicacion").add(post).addOnSuccessListener { documentReference ->
            post.id = documentReference.id
            db.collection("Publicacion").document(documentReference.id).set(post)
            showMessage()
        }
    }

    // Function to show error message
    private fun showError() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Debes completar todos los campos")
            .setPositiveButton("Aceptar", null)
            .create()
            .show()
    }

    // Function to show success message
    private fun showMessage() {
        AlertDialog.Builder(this)
            .setTitle("¡Genial!")
            .setMessage("Tu anuncio fue publicado")
            .setPositiveButton("Aceptar") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
            }
            .create()
            .show()
    }

    // Function to add prenda to Firestore
    private fun addPrendaFirebase() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val prendaRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = prendaRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            prendaRef.downloadUrl.addOnSuccessListener { uri ->
                val prenda = Prenda(
                    null,
                    binding.talle.text.toString(),
                    binding.colores.text.toString(),
                    binding.estado.selectedItem.toString(),
                    binding.material.text.toString(),
                    binding.modelo.text.toString(),
                    uri.toString()
                )
                val newDocRef = db.collection("Prendas").document()
                idPrendaGlobal = newDocRef.id
                prenda.id = idPrendaGlobal
                newDocRef.set(prenda)
            }
        }
    }
}

