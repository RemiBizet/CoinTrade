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
class TO52CoinActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            // Obtenir la base de données
            val db = DatabaseManager.getDatabase()
            // Utiliser la base de données (par exemple, obtenir le DAO)
            val userDao = db.userDao()
            // Mise en place des vues
            val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
            val dollarAmountTextView = findViewById<TextView>(R.id.dollarAmountTextView)
            val cryptoQuantityTextView = findViewById<TextView>(R.id.cryptoQuantityTextView)
            val transferButton = findViewById<Button>(R.id.transferButton)
            val tradeButton = findViewById<Button>(R.id.tradeButton)
            val logoutImageView = findViewById<ImageView>(R.id.logoutImageView)
            val bitcoinPriceView = findViewById<BitcoinPriceView>(R.id.bitcoinPriceView)
            val recipientEditText = findViewById<EditText>(R.id.recipientEditText)
            val amountEditText = findViewById<EditText>(R.id.amountEditText)

            // Récupération du prix du Bitcoin
            bitcoinPriceView.getBitcoinPrice()

            // Réception du pseudo de l'utilisateur
            val receivedUsername = intent.getStringExtra("username")
            val user = receivedUsername?.let { userDao.getUserByUsername(it) }

            val handler = Handler(Looper.getMainLooper())
            val myAppInstance = application as MyApp
            myAppInstance.chainTO52 = BlockchainTO52()
            val blockchainTO52 = myAppInstance.chainTO52

            // Sample wallets with key pairs
            val keyPair = blockchainTO52.generateKeyPair()
            val wallet = user?.let { WalletTO52(it.username,keyPair.public.toString(), keyPair.public, keyPair.private, 12.0) }

            lifecycleScope.launch {
                if (user != null) {
                    if (wallet != null) {
                        userDao.updateAddress(
                            user.username,
                            wallet.publicKey.toString()
                        )
                    }
                }

                handler.post {
                    val imageView: ImageView = findViewById(R.id.qrCodeImageView)
                    val qrCodeBitmap = QRCodeGenerator().generateQRCode(wallet?.publicKey.toString(), 650, 650)
                    imageView.setImageBitmap(qrCodeBitmap)
                }

                // Mise en place du listener sur le bouton gérant le lancement de l'activité pour envoyer des bitcoins
                transferButton.setOnClickListener {
                    lifecycleScope.launch {
                        val recipient = userDao.getUserByUsername(recipientEditText.text.toString())
                        if (recipient != null) {
                            var amount = amountEditText.text.toString().toDouble()
                            val walletRecipient = recipient?.let { WalletTO52(recipient.username,keyPair.public.toString(), keyPair.public, keyPair.private, 100.0) }

                            println("Sending ...")

                            if (user != null) {
                                if (wallet != null) {
                                    if (walletRecipient != null) {
                                        blockchainTO52.createTransaction(user.username,
                                            recipient.username, amount, wallet,walletRecipient )
                                    }
                                }
                            }
                            blockchainTO52.printBlockchain()

                            Toast.makeText(
                                this@TO52CoinActivity,
                                "Envoyé $amount BTC à ${recipient.username}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@TO52CoinActivity,
                                "Utilisateur non trouvé",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
                val intentLogout = Intent(this@TO52CoinActivity, LoginActivity::class.java)
                intentLogout.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentLogout)
                finish()
            }

            // Mise en place du listener sur le bouton Acheter/Vendre
            tradeButton.setOnClickListener {
                myAppInstance.chainTO52 = blockchainTO52
                val intentBuySell = Intent(this@TO52CoinActivity, BuySellActivity::class.java)
                intentBuySell.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentBuySell)
                finish()
            }
        }
    }
}