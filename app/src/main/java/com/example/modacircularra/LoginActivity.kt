package com.example.modacircularra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<AppCompatButton>(R.id.login)
        val register = findViewById<TextView>(R.id.register)
        val changePsw = findViewById<TextView>(R.id.change_psw)

        btnLogin.setOnClickListener {
            validate(email, password)
        }

        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        changePsw.setOnClickListener {
            val intent = Intent(this, ChangePswActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validate(email: EditText, password: EditText) {
        if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
            login(email, password)
        } else {
            showAlert()
        }
    }

    private fun login(email: EditText, password: EditText) {
        val auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (user?.isEmailVerified == true) {
                        // El email del usuario ha sido verificado
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        verificationError()
                    }

                } else {
                    showError()
                }
            }

    }

    private fun verificationError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verificación de correo electrónico requerida")
        builder.setMessage("Por favor, necesitamos que verifiques tu correo electrónico para poder continuar. Hemos enviado un enlace de verificación a tu bandeja de entrada. Si no lo ves, podría estar en la carpeta de spam. ¡Muchas gracias!")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Lo sentimos, no hemos podido iniciar sesión. Por favor, verifica que tu nombre de usuario y contraseña sean correctos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Por favor, completa los campos vacíos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}