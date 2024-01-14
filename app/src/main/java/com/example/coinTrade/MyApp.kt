package com.example.coinTrade;

import android.app.Application
import com.example.coinTrade.Database.DatabaseManager
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.PeerGroup

// Classe permettant la récupération de la blockchain n'importe où dans l'application
class MyApp : Application(){

    lateinit var chain: BlockChain
    lateinit var peerGroup : PeerGroup

    override fun onCreate(){
        super.onCreate()
        DatabaseManager.initialize(this)
    }
}

