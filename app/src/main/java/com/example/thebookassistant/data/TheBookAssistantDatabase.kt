package com.example.thebookassistant.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoritedBooks::class], version = 2, exportSchema = false)
abstract class TheBookAssistantDatabase : RoomDatabase() {

    abstract fun favoritedBooksDao(): FavoritedBooksDao

}