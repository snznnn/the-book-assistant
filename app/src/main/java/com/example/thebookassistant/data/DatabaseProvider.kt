package com.example.thebookassistant.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: TheBookAssistantDatabase? = null

    fun getDatabase(context: Context): TheBookAssistantDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TheBookAssistantDatabase::class.java,
                "thebookassistant_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }

}