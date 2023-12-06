package com.example.coinTrade

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.store.BlockStore
import org.bitcoinj.store.MemoryBlockStore


// Menu principal de l'application
class MainActivity : Activity() {


    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Mise en place des vues
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        val dollarAmountTextView = findViewById<TextView>(R.id.dollarAmountTextView)
        val cryptoQuantityTextView = findViewById<TextView>(R.id.cryptoQuantityTextView)
        val tradeButton = findViewById<Button>(R.id.tradeButton)
        val transferButton = findViewById<Button>(R.id.transferButton)
        val logoutImageView = findViewById<ImageView>(R.id.logoutImageView)
        val bitcoinPriceView = findViewById<BitcoinPriceView>(R.id.bitcoinPriceView)

        // Récupération du prix du Bitcoin depuis l'API Coingecko
        bitcoinPriceView.getBitcoinPrice()

        // Réception du pseudo de l'utilisateur
        val receivedUsername = intent.getStringExtra("username")

        // Construction de la base de données
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
        val userDao = db.userDao()
        val user = receivedUsername?.let { userDao.getUserByUsername(it) }

        // Récupération du Wallet de l'utilisateur
        val params = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
        val userWallet = UserWallet(params,"./wallets/${user?.username}/")
        val wallet = userWallet.getWallet()

        val myApp = application as MyApp

        val blockStore: BlockStore = MemoryBlockStore(params)
        myApp.chain = BlockChain(params, wallet, blockStore)
        myApp.peerGroup = PeerGroup(params,myApp.chain)
        myApp.peerGroup.addWallet(wallet)

        // Affichage des données de l'utilisateur
        if (user != null) {
            usernameTextView.text = user.username
            dollarAmountTextView.text = user.dollars.toString()
            cryptoQuantityTextView.text = wallet.balance.toString()
        }

        // Mise en place du listener sur le bouton gérant le lancement de l'activité pour envoyer des bitcoins
        transferButton.setOnClickListener {
            val intentTransfer = Intent(this, TransferActivity::class.java)
            if (user != null) {
                intent.putExtra("username", user.username)
            }
            startActivity(intentTransfer)
        }

        // Mise en place du listener sur le bouton gérant le lancement de l'activité pour acheter/vendre des bitcoins
        tradeButton.setOnClickListener {
            val intentTrade = Intent(this, TradeActivity::class.java)
            if (user != null) {
                intent.putExtra("username", user.username)
            }
            startActivity(intentTrade)
        }

        // Mise en place du listener sur le bouton gérant la déconnexion
        logoutImageView.setOnClickListener {
            val intentLogout = Intent(this, LoginActivity::class.java)
            intentLogout.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentLogout)
            finish()
        }

    }
}