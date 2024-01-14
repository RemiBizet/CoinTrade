package com.example.coinTrade

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinTrade.Database.DatabaseManager
import kotlinx.coroutines.launch
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.SegwitAddress
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.Wallet.SendResult
import java.io.File
import java.util.Date

// Menu principal de l'application
class MainActivity : AppCompatActivity() {
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
            val logoutImageView = findViewById<ImageView>(R.id.logoutImageView)
            val bitcoinPriceView = findViewById<BitcoinPriceView>(R.id.bitcoinPriceView)
            val recipientAddressEditText = findViewById<EditText>(R.id.recipientAddressEditText)
            val recipientEditText = findViewById<EditText>(R.id.recipientEditText)
            val amountEditText = findViewById<EditText>(R.id.amountEditText)

            // Récupération du prix du Bitcoin
            bitcoinPriceView.getBitcoinPrice()

            // Réception du pseudo de l'utilisateur
            val receivedUsername = intent.getStringExtra("username")
            val user = receivedUsername?.let { userDao.getUserByUsername(it) }

            // Connexion au TestNet
            val params = TestNet3Params.get()
            fun startWalletTask() {
                Thread {
                    // Création/Récupération du Wallet de l'utilisateur
                    val walletAppKit = object : WalletAppKit(params, File("${filesDir.path}/${user?.username}.dat"), user?.username) {
                        override fun onSetupCompleted() {
                            // Récupération des clefs, des adresses et du contenu du Wallet
                            if (wallet().importedKeys.size < 1) wallet().importKey(ECKey())
                            Log.d("myLogs", "My current address = " + wallet().currentReceiveAddress())
                            Log.d("myLogs", "Is my balance zero = " + wallet().balance.isZero)
                            Log.d("myLogs", "Wallet balance in BTC = " + wallet().balance.toBtc())
                            lifecycleScope.launch {
                                if (user != null) {
                                    userDao.updateAddress(
                                        user.username,
                                        wallet().currentReceiveAddress().toString()
                                    )
                                }
                            }
                            // user:ab
                            // user:ba

                            val ecKey = wallet().currentReceiveKey()
                            Log.d("myLogs", "My current receiveKey = $ecKey")
                            val segwitAddress = SegwitAddress.fromKey(params, ecKey)
                            Log.d("myLogs", "My current address in segWit format = $segwitAddress")

                            // tb1qyua6c8gr32ajftsrcxpret7u3ctmsemw6mhqz2 user:ab
                        }
                    }

                    // Pour observer le téléchargement de la blockchain
                    walletAppKit.setDownloadListener(object : DownloadProgressTracker() {
                        override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                            super.progress(pct, blocksSoFar, date)
                            val percentage = pct.toInt()
                            Log.d("myLogs", "Download of the blockchain $percentage")
                        }

                        // Quand la blocckchain a fini d'être chargée
                        override fun doneDownload() {

                            // Mise à jours des vues
                            if (user != null) {
                                dollarAmountTextView.text = "${user.dollars} $"
                            }
                            val wallet = walletAppKit.wallet()

                            cryptoQuantityTextView.text = "${wallet.balance.toBtc()} BTC"

                            /*
                            runOnUiThread {
                                val imageView: ImageView = findViewById(R.id.qrCodeImageView)
                                val qrCodeBitmap = QRCodeGenerator().generateQRCode(wallet.currentReceiveAddress().toString(), 500, 500)
                                imageView.setImageBitmap(qrCodeBitmap)
                            }
                            */


                            // Mise en place du listener sur le bouton gérant le lancement de l'activité pour envoyer des bitcoins
                            transferButton.setOnClickListener {
                                lifecycleScope.launch {
                                    val recipient = userDao.getUserByUsername(recipientEditText.text.toString())
                                    if (recipient != null) {
                                        var amount = amountEditText.text.toString().toDouble()
                                        val recipientAddress: Address = Address.fromString(params, recipientAddressEditText.text.toString())

                                        var amountToSend: Coin = Coin.valueOf((amount * Coin.COIN.value).toLong())


                                        // Frais de Transaction
                                        // amountToSend = amountToSend.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE)

                                        val sendResult: SendResult = wallet.sendCoins(
                                            walletAppKit.peerGroup(),
                                            recipientAddress,
                                            amountToSend
                                        )

                                        println("Sending ...")
                                        sendResult.broadcastComplete

                                        Toast.makeText(
                                            this@MainActivity,
                                            "Envoyé $amount BTC à ${recipient.username}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Utilisateur non trouvé",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            val keyTextView = findViewById<TextView>(R.id.keyTextView)
                            keyTextView.text =  wallet.currentReceiveAddress().toString()
                        }
                    })

                    walletAppKit.setBlockingStartup(false)
                    walletAppKit.startAsync()

                    // On attend que le wallet se synchronize avec la blockchain
                    walletAppKit.awaitRunning()
                    walletAppKit.peerGroup().downloadBlockChain()

                }.start()
            }

            // Lancement du Thread gérant le Wallet
            startWalletTask()

            if (user != null) {
                usernameTextView.text = user.username
            }
            // Mise en place du listener sur le bouton gérant la déconnexion
            logoutImageView.setOnClickListener {
                val intentLogout = Intent(this@MainActivity, LoginActivity::class.java)
                intentLogout.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentLogout)
                finish()
            }
        }
    }
}