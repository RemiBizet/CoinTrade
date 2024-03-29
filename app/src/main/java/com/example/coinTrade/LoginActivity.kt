package com.example.coinTrade

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinTrade.Database.DatabaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



// Menu d'authentification
class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: Button
    private lateinit var radioGroupSelectionMode: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Lancer une coroutine pour effectuer des opérations de base de données
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Obtenir la base de données
                val db = DatabaseManager.getDatabase()
                // Utiliser la base de données (par exemple, obtenir le DAO)
                val userDao = db.userDao()

                // Mise en place des vues
                usernameEditText = findViewById(R.id.editTextUsername)
                passwordEditText = findViewById(R.id.editTextPassword)
                loginButton = findViewById(R.id.buttonLogin)
                createAccountButton = findViewById(R.id.buttonCreateAccount)
                radioGroupSelectionMode = findViewById(R.id.radioGroupSelectionMode)

                // Mise en place du listener sur le bouton gérant l'authentification
                loginButton.setOnClickListener {
                    lifecycleScope.launch {
                        val username = usernameEditText.text.toString()
                        val password = passwordEditText.text.toString()

                        // Recherche de l'utilisateur dans la base de données
                        val user = userDao.getUserByUsernameAndPassword(username, password)

                        // Vérification des données saisies
                        if (user != null) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Authentification réussie",
                                Toast.LENGTH_SHORT
                            ).show()

                            val selectedRadioButtonId = radioGroupSelectionMode.checkedRadioButtonId
                            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)

                            if (selectedRadioButton != null) {
                                val selectedActivity = when (selectedRadioButton.id) {
                                    R.id.radioButtonTestNet -> MainActivity::class.java
                                    R.id.radioButtonRegTest -> RegTestActivity::class.java
                                    R.id.radioButtonTO52 -> TO52CoinActivity::class.java
                                    else -> MainActivity::class.java
                                }

                                // Lancement du mode sélectionné
                                val intent = Intent(this@LoginActivity, selectedActivity)
                                intent.putExtra("username", username)
                                startActivity(intent)

                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Chosissez un mode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Nom d'utilisateur ou mot de passe incorrect",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                createAccountButton.setOnClickListener {
                    val intent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
