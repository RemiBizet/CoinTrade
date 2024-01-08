package com.example.coinTrade

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coinTrade.Database.DatabaseManager
import com.example.coinTrade.Database.UserWallet
import kotlinx.coroutines.launch
import org.bitcoinj.core.Coin
import org.bitcoinj.core.NetworkParameters

// Activité pour envoyer des bitcoins
class TradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)

        // Mise en place des vues
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        val dollarAmountTextView = findViewById<TextView>(R.id.dollarAmountTextView)
        val cryptoQuantityTextView = findViewById<TextView>(R.id.cryptoQuantityTextView)
        val bitcoinPriceView = findViewById<BitcoinPriceView>(R.id.bitcoinPriceView)
        val montantEdit = findViewById<EditText>(R.id.montantEdit)
        val buyButton = findViewById<Button>(R.id.buyButton)
        val sellButton = findViewById<Button>(R.id.sellButton)

        // Récupération du prix du Bitcoin depuis l'API Coingecko
        bitcoinPriceView.getBitcoinPrice()
        val bitcoinPrice: Double = bitcoinPriceView.text.toString().toDouble()

        // Réception du pseudo de l'utilisateur
        val receivedUsername = intent.getStringExtra("username")

        lifecycleScope.launch {
            // Obtenir la base de données
            val db = DatabaseManager.getDatabase()
            // Utiliser la base de données (par exemple, obtenir le DAO)
            val userDao = db.userDao()
            val user = receivedUsername?.let { userDao.getUserByUsername(it) }

            // Récupération du Wallet de l'utilisateur
            val userWallet = UserWallet(
                NetworkParameters.fromID(NetworkParameters.ID_TESTNET),
                "./wallets/${user?.username}/"
            )
            val wallet = userWallet.getWallet()

            // Affichage des données de l'utilisateur
            if (user != null) {
                usernameTextView.text = user.username
                dollarAmountTextView.text = user.dollars.toString()
                cryptoQuantityTextView.text = wallet.balance.toString()
            }

            val amount: Double = if (montantEdit.text.isNotEmpty()) {
                montantEdit.text.toString().toDouble()
            } else {
                0.0
            }

            // Mise en place du listener sur le bouton gérant le lancement de l'activité pour envoyer des bitcoins
            buyButton.setOnClickListener {
                if (user != null && amount != 0.0) {

                    val bitcoinAmount = amount / bitcoinPrice
                    val satoshisAmount = Coin.parseCoin(bitcoinAmount.toString())
                    user.dollars -= amount
                    wallet.balance.add(satoshisAmount)
                    dollarAmountTextView.text = user.dollars.toString()
                }
                cryptoQuantityTextView.text = wallet.balance.toString()
            }

            // Mise en place du listener sur le bouton gérant le lancement de l'activité pour acheter/vendre des bitcoins
            sellButton.setOnClickListener {
                if (user != null && wallet.balance.toString().toDouble() != 0.0) {

                    val dollarsAmount = amount * bitcoinPrice
                    val satoshisAmount = Coin.parseCoin(amount.toString())
                    user.dollars += dollarsAmount
                    wallet.balance.minus(satoshisAmount)
                    dollarAmountTextView.text = user.dollars.toString()
                }
                cryptoQuantityTextView.text = wallet.balance.toString()
            }
        }
    }
}