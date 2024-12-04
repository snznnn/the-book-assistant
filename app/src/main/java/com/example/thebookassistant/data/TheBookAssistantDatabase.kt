package com.example.thebookassistant.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.thebookassistant.data.dao.FavoritedBooksDao
import com.example.thebookassistant.data.entity.FavoritedBooks

@Database(entities = [FavoritedBooks::class], version = 2, exportSchema = false)
abstract class TheBookAssistantDatabase : RoomDatabase() {

    abstract fun favoritedBooksDao(): FavoritedBooksDao

}