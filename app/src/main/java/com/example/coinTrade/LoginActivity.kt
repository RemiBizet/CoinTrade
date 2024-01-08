package com.example.coinTrade

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinTrade.Database.DatabaseManager
import kotlinx.coroutines.launch


// Menu de d'authentification
class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Lancer une coroutine pour effectuer des opérations de base de données
        lifecycleScope.launch {
            // Obtenir la base de données
            val db = DatabaseManager.getDatabase()
            // Utiliser la base de données (par exemple, obtenir le DAO)
            val userDao = db.userDao()

            // Mise en place des vues
            usernameEditText = findViewById(R.id.editTextUsername)
            passwordEditText = findViewById(R.id.editTextPassword)
            loginButton = findViewById(R.id.buttonLogin)
            createAccountButton = findViewById(R.id.buttonCreateAccount)


            // Mise en place du listener sur le bouton gérant l'authentification
            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                // Recherche de l'utilisateur dans la base de données
                val user = userDao.getUserByUsernameAndPassword(username, password)

                // Vérification des données saisies
                if (user != null) {
                    Toast.makeText(this@LoginActivity, "Authentification réussie", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("username", user.username)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Nom d'utilisateur ou mot de passe incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            createAccountButton.setOnClickListener {
                val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
