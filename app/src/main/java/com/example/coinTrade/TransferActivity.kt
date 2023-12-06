package com.example.coinTrade

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.room.Room
import org.bitcoinj.core.Coin
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.wallet.Wallet

// Activité pour acheter/vendre des bitcoins
class TransferActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)

        // Mise en place des vues
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        val bitcoinPriceView = findViewById<BitcoinPriceView>(R.id.bitcoinPriceView)
        val dollarAmountTextView = findViewById<TextView>(R.id.dollarAmountTextView)
        val cryptoQuantityTextView = findViewById<TextView>(R.id.cryptoQuantityTextView)

        // Récupération du prix du Bitcoin depuis l'API Coingecko
        bitcoinPriceView.getBitcoinPrice()

        // Réception du pseudo de l'utilisateur
        val receivedUsername = intent.getStringExtra("username")

        // Building the Room database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
        val userDao = db.userDao()
        val user = receivedUsername?.let { userDao.getUserByUsername(it) }

        // Récupération du Wallet de l'utilisateur
        val userWallet = UserWallet(
            NetworkParameters.fromID(NetworkParameters.ID_TESTNET),
            "./wallets/${user?.username}/"
        ).getWallet()

        // Setting up the views according to the user
        if (user != null) {
            usernameTextView.text = user.username
            dollarAmountTextView.text = user.dollars.toString()
            cryptoQuantityTextView.text = userWallet.balance.toString()
        }

        val myApp = application as MyApp

        fun transferFunds(senderUsername: String, receiverUsername: String, amount: Long) {
            val sender = userDao.getUserByUsername(senderUsername)
            val recipient = userDao.getUserByUsername(receiverUsername)

            if (sender != null && recipient != null) {

                var senderWallet = UserWallet(
                    NetworkParameters.fromID(NetworkParameters.ID_TESTNET),
                    "./wallets/${senderUsername}/"
                ).getWallet()
                val recipientWallet = UserWallet(
                    NetworkParameters.fromID(NetworkParameters.ID_TESTNET),
                    "./wallets/${receiverUsername}/"
                ).getWallet()

                val recipientAddress = recipientWallet.currentReceiveAddress()
                val amountToSend: Coin = Coin.valueOf(amount)
                val result: Wallet.SendResult =
                    senderWallet.sendCoins(myApp.peerGroup, recipientAddress, amountToSend)
                result.broadcastComplete.get()
            } else {
                println("Invalid sender or recipient.")
            }
        }
    }
}