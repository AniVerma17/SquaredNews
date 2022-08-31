package com.example.squarednews.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.squarednews.data.Article

@Database(version = 1, entities = [Article::class], exportSchema = false)
abstract class NewsDb : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}