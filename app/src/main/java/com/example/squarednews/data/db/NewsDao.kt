package com.example.squarednews.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.example.squarednews.data.Article

@Dao
interface NewsDao {
    @Query("SELECT * FROM article")
    fun getArticles(): PagingSource<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(articlesList: List<Article>)

    @Query("DELETE FROM article")
    suspend fun clearAllArticles()
}