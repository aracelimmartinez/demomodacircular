package com.example.modacircularra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class ChangePswActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ChangePswActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_psw)

        val email = findViewById<EditText>(R.id.email)
        val btnChange = findViewById<AppCompatButton>(R.id.change_psw)

        btnChange.setOnClickListener {
            validate(email)
        }
    }

    private fun validate(email: EditText) {
        if (email.text.isNotEmpty()) {
            changePsw(email)
        } else {
            showAlert()
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Por favor, introduce un correo electrónico válido")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun changePsw(email: EditText) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Email sent.")
                showMsg(email)
            } else {
                showAlert()
            }
        }
    }

    private fun showMsg(email: EditText) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email enviado")
        builder.setMessage("Te enviamos un email a ${email.text}. Por favor, verifica tu bandeja de entrada")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
