package com.example.coinTrade;

import android.app.Application
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.PeerGroup

class MyApp : Application(){

    lateinit var chain: BlockChain
    lateinit var peerGroup : PeerGroup

    override fun onCreate(){
        super.onCreate()
    }
}

