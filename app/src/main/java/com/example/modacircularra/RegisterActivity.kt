package com.example.modacircularra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<TextView>(R.id.register)
        val name = findViewById<EditText>(R.id.name)
        val lastname = findViewById<EditText>(R.id.lastname)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val repeatPassword = findViewById<EditText>(R.id.repeatPassword)

        btnRegister.setOnClickListener {
            validate(name, lastname, email, password, repeatPassword)
        }

    }

    private fun validate(
        name: EditText,
        lastname: EditText,
        email: EditText,
        password: EditText,
        repeatPassword: EditText
    ) {

        val passwordPattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+.=])(?=\\S+$).{8,12}$")
        val passwordMatcher = passwordPattern.matcher(password.text.toString())

        if (name.text.isNotEmpty() && lastname.text.isNotEmpty() && email.text.isNotEmpty() && password.text.isNotEmpty() && repeatPassword.text.isNotEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                showEmailError()
            } else if (password.text.toString() != repeatPassword.text.toString()) {
                showPswAlert()
            } else if (!passwordMatcher.matches()) {
                showPswError()
            } else {
                authenticator(name, lastname, email, password)
            }
        } else {
            showAlert()
        }
    }

    private fun authenticator(
        name: EditText, lastname: EditText, email: EditText, password: EditText
    ) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Enviar email de verificacion
                    sendVerificationEmail(auth)
                    //Guardar nuevo usuario en la base de datos
                    addUserFirebase(auth, db, name, lastname, email)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e("FirebaseAuth", task.exception?.message ?: "Unknown error")
                }
            }
    }

    private fun addUserFirebase(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        name: EditText,
        lastname: EditText,
        email: EditText
    ) {
        val userID = auth.currentUser!!.uid
        val userData = hashMapOf(
            "nombre" to name.text.toString(),
            "apellido" to lastname.text.toString(),
            "email" to email.text.toString()
        )
        // Agregar un nuevo documento a la collection "Usuarios" con su id y data
        db.collection("Usuarios").document(userID).set(userData)
    }

    private fun sendVerificationEmail(auth: FirebaseAuth) {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("EmailVerification", "Email sent.")
                }
            }
    }

    private fun showPswError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("La contraseña debe tener al menos 8 caracteres, una letra mayúscula, un número y un símbolo especial")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showEmailError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Por favor, introduce un correo electrónico válido")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showPswAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Las contraseñas no coinciden")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al querer registrar el usuario. Hay campos vacíos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}