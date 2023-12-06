package com.example.coinTrade

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import org.bitcoinj.core.NetworkParameters

// Menu de la création de compte
class CreateAccountActivity : Activity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var creerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // Initialisation de la base de données
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()

        val userDao = db.userDao()

        // Mise en place des vues
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        creerButton = findViewById(R.id.buttonCreer)

        // Mise en place du listener sur le bouton gérant la création de comptes
        creerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Recherche de l'utilisateur dans la base de données
            val user = userDao.getUserByUsername(username, password)
            // Vérification de l'existence préalable de l'utilisateur
            if (user != null) {
                Toast.makeText(this, "Nom d'utilisateur déjà utilisé/ Compte déjà existant", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Compte crée", Toast.LENGTH_SHORT).show()
                val newWallet = UserWallet(NetworkParameters.fromID(NetworkParameters.ID_TESTNET),"./wallets/$username/")
                val newUser = User(username = username,password = password, dollars = 100.0, walletPath = "./wallets/$username/")
                userDao.insert(newUser)
            }
        }
    }
}
