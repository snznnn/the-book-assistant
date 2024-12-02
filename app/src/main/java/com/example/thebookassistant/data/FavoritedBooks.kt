package com.example.thebookassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "favorited_books")
data class FavoritedBooks(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val authors: String,

    val key: String

)

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorited_books ADD COLUMN key TEXT NOT NULL DEFAULT ''")
    }
}