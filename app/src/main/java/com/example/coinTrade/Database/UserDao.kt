package com.example.coinTrade.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.coinTrade.Database.User

// Interface de la base de données
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUserByUsernameAndPassword(username: String, password: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("UPDATE users SET dollars = :newBalance WHERE username = :username")
    suspend fun updateDollars(username: String, newBalance: Double)

    @Query("UPDATE users SET walletPath = :newwalletAddress WHERE username = :username")
    suspend fun updateAddress(username: String, newwalletAddress: String)
}
