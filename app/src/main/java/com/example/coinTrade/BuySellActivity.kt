package com.example.coinTrade

import BlockchainTO52
import WalletTO52
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinTrade.Database.DatabaseManager
import kotlinx.coroutines.launch


// Menu principal de l'application
class BuySellActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_buysell)

        lifecycleScope.launch {
            // Obtenir la base de données
            val db = DatabaseManager.getDatabase()
            // Utiliser la base de données (par exemple, obtenir le DAO)
            val userDao = db.userDao()
            // Mise en place des vues
            val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
            val dollarAmountTextView = findViewById<TextView>(R.id.dollarAmountTextView)
            val cryptoQuantityTextView = findViewById<TextView>(R.id.cryptoQuantityTextView)
            val logoutImageView = findViewById<ImageView>(R.id.logoutImageView)
            val bitcoinPriceView = findViewById<BitcoinPriceView>(R.id.bitcoinPriceView)
            val amountEditText = findViewById<EditText>(R.id.amountEditText)
            val buyButton = findViewById<Button>(R.id.buyButton)
            val sellButton = findViewById<Button>(R.id.sellButton)

            // Récupération du prix du Bitcoin
            bitcoinPriceView.getBitcoinPrice()

            // Réception du pseudo de l'utilisateur
            val receivedUsername = intent.getStringExtra("username")
            val user = receivedUsername?.let { userDao.getUserByUsername(it) }

            val myAppInstance = application as MyApp
            val blockchainTO52 = myAppInstance.chainTO52

            // Sample wallets with key pairs
            val keyPair = blockchainTO52.generateKeyPair()
            val wallet = user?.let { WalletTO52(it.username,keyPair.public.toString(), keyPair.public, keyPair.private, 152.0) }

            lifecycleScope.launch {
                if (user != null) {
                    if (wallet != null) {
                        userDao.updateAddress(
                            user.username,
                            wallet.publicKey.toString()
                        )
                    }
                }
            }

            // Mise à jours des vues
            if (user != null) {
                usernameTextView.text = user.username
                dollarAmountTextView.text = "${user.dollars} $"
                if (wallet != null) {
                    cryptoQuantityTextView.text = "${wallet.balance} TO52Coins"
                }
            }
            // Mise en place du listener sur le bouton gérant la déconnexion
            logoutImageView.setOnClickListener {
                val intentLogout = Intent(this@BuySellActivity, LoginActivity::class.java)
                intentLogout.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentLogout)
                finish()
            }

            val amount: Double = if (amountEditText .text.isNotEmpty()) {
                amountEditText .text.toString().toDouble()
            } else {
                0.0
            }
            val bitcoinPrice: Double = bitcoinPriceView.text.toString().toDouble()
            // Mise en place du listener sur le bouton gérant le lancement de l'activité pour envoyer des bitcoins
            buyButton.setOnClickListener {
                if (user != null && amount != 0.0) {

                    val bitcoinAmount = amount/bitcoinPrice
                    user.dollars -= amount
                    dollarAmountTextView.text = user.dollars.toString()
                }
                // cryptoQuantityTextView.text = wallet.balance.toString()
            }

            // Mise en place du listener sur le bouton gérant le lancement de l'activité pour acheter/vendre des bitcoins
            sellButton.setOnClickListener {
                if (user != null && amount != 0.0) {
                    val dollarsAmount = amount * bitcoinPrice
                    user.dollars += dollarsAmount
                    dollarAmountTextView.text = user.dollars.toString()
                }
                // cryptoQuantityTextView.text = wallet.balance.toString()
            }
        }
    }
}