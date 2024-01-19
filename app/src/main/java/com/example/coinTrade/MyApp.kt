package com.example.coinTrade;

import BlockchainTO52
import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.coinTrade.Database.DatabaseManager


// Classe permettant la récupération de la blockchain n'importe où dans l'application
@SuppressLint("NewApi")
class MyApp : Application(){

    var chainTO52: BlockchainTO52 = BlockchainTO52()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(){
        super.onCreate()
        DatabaseManager.initialize(this)
        val blockchainManager = BlockchainManager.getInstance()

        // If not already set, initialize the blockchain
        if (!blockchainManager.isBlockchainInitialized()) {
            val chainTO52 = BlockchainTO52() // Initialize your blockchain here
            blockchainManager.setBlockchain(chainTO52)
        }

    }
}

class BlockchainManager private constructor() {

    private lateinit var chainTO52: BlockchainTO52

    companion object {
        @Volatile
        private var instance: BlockchainManager? = null

        fun getInstance(): BlockchainManager {
            return instance ?: synchronized(this) {
                instance ?: BlockchainManager().also { instance = it }
            }
        }
    }

    fun getBlockchain(): BlockchainTO52 {
        return chainTO52
    }

    fun setBlockchain(blockchain: BlockchainTO52) {
        this.chainTO52 = blockchain
    }

    fun isBlockchainInitialized(): Boolean {
        return ::chainTO52.isInitialized
    }
}
