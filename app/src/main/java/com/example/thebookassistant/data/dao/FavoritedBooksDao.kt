package com.example.thebookassistant.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thebookassistant.data.entity.FavoritedBooks
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritedBooksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteBook(book: FavoritedBooks): Long

    @Query("SELECT * FROM favorited_books")
    fun getAllFavoriteBooks(): Flow<List<FavoritedBooks>>

    @Delete
    suspend fun deleteFavoriteBook(book: FavoritedBooks)

    @Query("DELETE FROM favorited_books WHERE `key` = :key")
    suspend fun deleteFavoriteBookByKey(key: String)

}