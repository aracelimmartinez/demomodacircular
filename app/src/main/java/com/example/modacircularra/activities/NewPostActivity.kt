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

    private lateinit var binding: ActivityNewPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var imageUri: Uri
    private lateinit var idPrendaGlobal: String

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.image.setImageURI(uri)
                imageUri = uri
            } else {
                Toast.makeText(this, "No se seleccionó una imagen.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        // Spinners initializations
        val estadoItems = arrayOf("Usado", "Poco uso", "Sin uso")
        val talleItems = arrayOf("XS", "S", "M", "L", "XL")
        val colorItems =
            arrayOf("Negro", "Blanco", "Amarillo", "Azul", "Naranja", "Rojo", "Verde", "Violeta")
        val materialItems = arrayOf("Algodón", "Poliéster", "Lana", "Cuero", "Jean")

        // Configurar adaptadores
        binding.estado.adapter = ArrayAdapter(this, R.layout.spinner_item, estadoItems)
        binding.talle.adapter = ArrayAdapter(this, R.layout.spinner_item, talleItems)
        binding.color.adapter = ArrayAdapter(this, R.layout.spinner_item, colorItems)
        binding.material.adapter = ArrayAdapter(this, R.layout.spinner_item, materialItems)

        binding.btnNext.setOnClickListener { validateFirstForm() }
        binding.btnPost.setOnClickListener { validateSecondForm() }
    }

    private fun validateFirstForm() {
        if (binding.talle.selectedItem != null &&
            binding.color.selectedItem != null &&
            binding.material.selectedItem != null &&
            binding.modelo.text.isNotEmpty()
        ) {
            addPrendaFirebase()
            switchToSecondForm()
        } else {
            showError()
        }
    }

    private fun switchToSecondForm() {
        binding.text.visibility = View.GONE
        binding.text2.visibility = View.GONE
        binding.text3.visibility = View.GONE
        binding.text4.visibility = View.GONE
        binding.estado.visibility = View.GONE
        binding.talle.visibility = View.GONE
        binding.color.visibility = View.GONE
        binding.material.visibility = View.GONE
        binding.modelo.visibility = View.GONE
        binding.btnNext.visibility = View.GONE
        binding.titulo.visibility = View.VISIBLE
        binding.descripcion.visibility = View.VISIBLE
        binding.precio.visibility = View.VISIBLE
        binding.btnPost.visibility = View.VISIBLE
    }

    private fun validateSecondForm() {
        val precioTexto = binding.precio.text.toString()
        val precio = precioTexto.toDoubleOrNull()

        if (binding.titulo.text.isNotEmpty() && binding.descripcion.text.isNotEmpty() && precio != null) {
            addPost()
        } else {
            if (precio == null) {
                Toast.makeText(this, "El precio no es válido", Toast.LENGTH_LONG).show()
            } else {
                showError()
            }
        }
    }

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

    private fun showError() {
        Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_LONG).show()
    }

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

    private fun addPrendaFirebase() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val prendaRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = prendaRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            prendaRef.downloadUrl.addOnSuccessListener { uri ->
                val prenda = Prenda(
                    null,
                    binding.talle.selectedItem.toString(),
                    binding.color.selectedItem.toString(),
                    binding.estado.selectedItem.toString(),
                    binding.material.selectedItem.toString(),
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

