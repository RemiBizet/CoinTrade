package com.example.coinTrade.Database

import android.content.Context
import androidx.room.Room

object DatabaseManager {
    private var instance: AppDatabase? = null

    fun initialize(context: Context) {
        instance = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }

    fun getDatabase(): AppDatabase {
        return instance ?: throw IllegalStateException("Database not initialized")
    }
}