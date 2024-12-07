package com.example.thebookassistant.data

import android.content.Context
import androidx.room.Room
import com.example.thebookassistant.data.entity.MIGRATION_1_2

object DatabaseProvider {

    @Volatile
    private var INSTANCE: TheBookAssistantDatabase? = null

    fun getDatabase(context: Context): TheBookAssistantDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TheBookAssistantDatabase::class.java,
                "thebookassistant_database"
            ).addMigrations(MIGRATION_1_2).build()
            INSTANCE = instance
            instance
        }
    }
}