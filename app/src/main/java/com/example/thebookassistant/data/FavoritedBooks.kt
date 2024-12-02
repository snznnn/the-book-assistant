package com.example.thebookassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorited_books")
data class FavoritedBooks(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val authors: String

)