package com.example.coinTrade;

import BlockchainTO52
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.coinTrade.Database.DatabaseManager


// Classe permettant la récupération de la blockchain n'importe où dans l'application
class MyApp : Application(){

    lateinit var chainTO52: BlockchainTO52

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(){
        super.onCreate()
        DatabaseManager.initialize(this)
    }
}

