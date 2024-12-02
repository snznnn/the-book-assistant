package com.example.thebookassistant.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritedBooksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteBook(book: FavoritedBooks): Long

    @Query("SELECT * FROM favorited_books")
    fun getAllFavoriteBooks(): Flow<List<FavoritedBooks>>

}