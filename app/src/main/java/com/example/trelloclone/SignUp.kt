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

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var signupButton: Button
    private lateinit var loginLink: TextView  // Déclarer la TextView pour le lien

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialiser Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Lier les vues avec les éléments XML
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        errorTextView = findViewById(R.id.errorTextView)
        signupButton = findViewById(R.id.signupButton)
        loginLink = findViewById(R.id.loginLink)  // Lier le lien

        // Ajouter un clic listener pour le bouton d'inscription
        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validation des champs
            if (email.isEmpty() || password.isEmpty()) {
                errorTextView.text = "Veuillez remplir tous les champs"
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
                registerUser(email, password)
            }
        }

        // Ajouter un clic listener pour le lien de redirection vers l'écran de connexion
        loginLink.setOnClickListener {
            // Rediriger l'utilisateur vers la page de connexion
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Fonction pour inscrire l'utilisateur
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // L'utilisateur a été créé avec succès
                    val user = auth.currentUser
                    Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show()
                    // Vous pouvez rediriger l'utilisateur vers l'écran principal après l'inscription réussie
                    // startActivity(Intent(this, MainActivity::class.java))
                    // finish()
                } else {
                    // Erreur lors de la création du compte
                    val errorMessage = task.exception?.message
                    errorTextView.text = errorMessage
                    errorTextView.visibility = View.VISIBLE
                }
            }
    }
}
