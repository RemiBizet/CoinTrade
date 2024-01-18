package com.example.coinTrade;

import BlockchainTO52
import android.app.Application
import com.example.coinTrade.Database.DatabaseManager


// Classe permettant la récupération de la blockchain n'importe où dans l'application
class MyApp : Application(){

    lateinit var chainTO52: BlockchainTO52

    override fun onCreate(){
        super.onCreate()
        DatabaseManager.initialize(this)
    }
}

