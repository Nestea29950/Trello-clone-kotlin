package com.example.trelloclone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var loginButton: Button
    private lateinit var signupLink: TextView // Lien vers inscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialiser Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Lier les vues avec les éléments XML
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        errorTextView = findViewById(R.id.errorTextView)
        loginButton = findViewById(R.id.loginButton)
        signupLink = findViewById(R.id.signupLink)

        // Ajouter un clic listener pour le bouton de connexion
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validation des champs
            if (email.isEmpty() || password.isEmpty()) {
                errorTextView.text = "Veuillez remplir tous les champs"
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
                loginUser(email, password)
            }
        }

        // Ajouter un clic listener pour le lien de redirection vers l'écran d'inscription
        signupLink.setOnClickListener {
            // Rediriger l'utilisateur vers la page d'inscription
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    // Fonction pour connecter l'utilisateur
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // L'utilisateur a été connecté avec succès
                    val user = auth.currentUser
                    Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()

                    // Rediriger l'utilisateur vers l'écran principal (ActivityAccueil)
                    startActivity(Intent(this, accueil::class.java))
                    finish() // Fermer l'écran de connexion pour éviter qu'il soit accessible en revenant
                } else {
                    // Erreur lors de la connexion
                    val errorMessage = task.exception?.message
                    errorTextView.text = errorMessage
                    errorTextView.visibility = View.VISIBLE
                }
            }
    }
}
