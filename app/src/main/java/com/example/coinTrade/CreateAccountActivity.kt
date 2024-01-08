package com.example.coinTrade

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coinTrade.Database.User
import com.example.coinTrade.Database.UserWallet
import org.bitcoinj.core.NetworkParameters
import androidx.lifecycle.lifecycleScope
import com.example.coinTrade.Database.DatabaseManager
import kotlinx.coroutines.launch

// Menu de la création de compte
class CreateAccountActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var creerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        lifecycleScope.launch {
            // Obtenir la base de données
            val db = DatabaseManager.getDatabase()
            // Utiliser la base de données (par exemple, obtenir le DAO)
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
                val user = userDao.getUserByUsernameAndPassword(username, password)
                // Vérification de l'existence préalable de l'utilisateur
                if (user != null) {
                    Toast.makeText(
                        this@CreateAccountActivity,
                        "Nom d'utilisateur déjà utilisé/ Compte déjà existant",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(this@CreateAccountActivity, "Compte crée", Toast.LENGTH_SHORT).show()
                    val newWallet = UserWallet(
                        NetworkParameters.fromID(NetworkParameters.ID_TESTNET),
                        "./wallets/$username/"
                    )
                    val newUser = User(
                        username = username,
                        password = password,
                        dollars = 100.0,
                        walletPath = "./wallets/$username/"
                    )
                    userDao.insert(newUser)
                }
            }
        }
    }
}
