package com.example.coinTrade

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.wallet.Wallet
import java.io.File

// Classe représentant le Wallet d'un utilisateur
class UserWallet(private val networkParams: NetworkParameters?, private val walletPath: String) {
    private val kit: WalletAppKit = WalletAppKit(networkParams, File(walletPath), "wallet")

    init {
        kit.connectToLocalHost();
        kit.startAsync()
        kit.awaitRunning()
    }

    fun getWallet(): Wallet {
        return kit.wallet()
    }
}