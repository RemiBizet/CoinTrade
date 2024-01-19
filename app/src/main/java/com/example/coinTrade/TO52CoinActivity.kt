package com.example.coinTrade

import BlockchainTO52
import TransactionTO52
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
import java.util.NoSuchElementException


// Menu principal de l'application
class TO52CoinActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_to52)

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

            val addressTextView = findViewById<TextView>(R.id.AddressTextView)
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

            val blockchainManager = BlockchainManager.getInstance()
            val blockchainTO52  = blockchainManager.getBlockchain()

            lateinit var wallet: WalletTO52

            // Création ou récupération du wallet de l'utilisateur
            if (user != null) {
                if (!blockchainTO52.doesUsernameExistInBlockhain(user.username)) {
                    blockchainTO52.add_wallet(user.username)
                }

                wallet = blockchainTO52.wallets.find { it.username == user.username }
                    ?: throw NoSuchElementException("Wallet de l'utilisateur non trouvé")

                println(wallet.address)

                lifecycleScope.launch {

                    handler.post {
                        val imageView: ImageView = findViewById(R.id.qrCodeImageView)
                        val qrCodeBitmap = QRCodeGenerator().generateQRCode(wallet.address, 650, 650)
                        imageView.setImageBitmap(qrCodeBitmap)
                    }

                    // Mise en place du listener sur le bouton gérant le lancement de l'activité pour envoyer des bitcoins
                    transferButton.setOnClickListener {
                        lifecycleScope.launch {
                            val recipient = userDao.getUserByUsername(recipientEditText.text.toString())
                            if (recipient != null) {
                                var amount = amountEditText.text.toString().toDouble()
                                var walletRecipient = blockchainTO52.wallets.find { it.username == recipient.username }
                                if (walletRecipient != null) {
                                    println(walletRecipient)
                                    println("Sending ...")

                                    val transaction =
                                        TransactionTO52(user.username, recipient.username, amount)
                                    wallet.balance -= amount
                                    walletRecipient.balance += amount
                                    val lastBlock = blockchainTO52.blockchain.last()
                                    val updatedTransactions = lastBlock.transactions.toMutableList()

                                    updatedTransactions.add(transaction)
                                    if (walletRecipient != null) {
                                        blockchainTO52.addBlock(
                                            updatedTransactions,
                                            lastBlock.hash,
                                            wallet,
                                            walletRecipient
                                        )
                                    }

                                    Toast.makeText(
                                        this@TO52CoinActivity,
                                        "Envoyé $amount BTC à ${recipient.username}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    cryptoQuantityTextView.text = "${wallet.balance} TO52Coins"

                                    println("List des wallets présents" + blockchainTO52.wallets)
                                    println("Wallet receipt " + walletRecipient.username)

                                    blockchainTO52.printBlockchain()
                                }


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
                usernameTextView.text = user.username
                dollarAmountTextView.text = "${user.dollars} $"
                cryptoQuantityTextView.text = "${wallet.balance} TO52Coins"
                addressTextView.text = wallet.address


                // Mise en place du listener sur le bouton gérant la déconnexion
                logoutImageView.setOnClickListener {
                    val intentLogout = Intent(this@TO52CoinActivity, LoginActivity::class.java)
                    intentLogout.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentLogout)
                    finish()
                }

                // Mise en place du listener sur le bouton Acheter/Vendre
                /*tradeButton.setOnClickListener {
                    val intentBuySell = Intent(this@TO52CoinActivity, BuySellActivity::class.java)
                    intentBuySell.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentBuySell)
                    finish()
                }*/
            }
        }
    }
}