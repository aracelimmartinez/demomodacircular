package com.example.modacircularra.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.modacircularra.R
import com.example.modacircularra.activities.MainActivity
import com.example.modacircularra.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialization
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        callbackManager = CallbackManager.Factory.create()
        val currentUser = auth.currentUser

        // Verify if a user is already logged
        if (currentUser != null) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }

        // Login button
        binding!!.login.setOnClickListener {
            val email = binding!!.email.text.toString()
            val password = binding!!.password.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmail(email, password)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Por favor, ingrese su email y contraseña",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Change psw button
        binding!!.changePsw.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChangePswFragment())
                .addToBackStack(null)
                .commit()
        }

        // Register button
        binding!!.register.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        // Google button
        binding!!.gmail.setOnClickListener {
            signInWithGoogle()
        }

        // Facebook button
        binding!!.facebook.setOnClickListener {

            Toast.makeText(requireContext(), "Función en construcción.", Toast.LENGTH_SHORT)
                .show()

            /*

            FacebookSdk.sdkInitialize(requireActivity().application)  // Usamos requireActivity().application
            AppEventsLogger.activateApp(requireActivity().application)

            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        handleFacebookAccessToken(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        Toast.makeText(
                            requireContext(),
                            "Inicio de sesión cancelado",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onError(error: FacebookException) {
                        Toast.makeText(
                            requireContext(),
                            "Error en login: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })

             */
        }
    }

    // Function to sign-in using Google
    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    // Function to sign-in using email
    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error, datos incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // Google authentication
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Store user in database
                        checkAndCreateUserInFirestore(user)
                    }
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error de autenticación con Firebase",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // Function to store user in database
    private fun checkAndCreateUserInFirestore(user: FirebaseUser) {
        val userEmail = user.email ?: return
        val usuariosRef = db.collection("Usuarios")

        usuariosRef.whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val nombreCompleto = user.displayName ?: "Usuario"
                    val parts = nombreCompleto.split(" ")
                    val nombre = if (parts.isNotEmpty()) parts[0] else nombreCompleto
                    val apellido = if (parts.size > 1) parts.last() else ""

                    val nuevoUsuario = hashMapOf(
                        "id" to user.uid,
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "email" to userEmail
                    )

                    usuariosRef.document(user.uid).set(nuevoUsuario)
                        .addOnSuccessListener {
                            // User data is store in database
                        }
                }
            }
    }

    // Facebook authentication
    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Store user in database
                        checkAndCreateUserInFirestore(user)
                    }
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error de autenticación con Firebase",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Error al iniciar sesión con Google",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}

