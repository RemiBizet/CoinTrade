package com.example.coinTrade

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Interface de la base de données
@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun getUserByUsernameAndPassword(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String, password: String): User?
}
