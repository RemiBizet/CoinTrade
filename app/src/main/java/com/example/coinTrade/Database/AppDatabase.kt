package com.example.coinTrade.Database

import androidx.room.Database
import androidx.room.RoomDatabase

// Classe de la base de données Dao
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}