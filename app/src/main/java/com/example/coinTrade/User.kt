package com.example.coinTrade

import androidx.room.Entity
import androidx.room.PrimaryKey

// Classe représentant un utilisateur
@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val password: String,
    var dollars: Double,
    val walletPath: String
)
