package com.example.newsapp.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.news.ArticleX

// Connect to -> Retrofit(data(articlex))
// as command
@Dao
interface NewsDAO {

    @Query("SELECT * FROM tbArticle")
    fun getArticles(): LiveData<List<ArticleX>> //from saved article(bookmark)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkedNews(articleX: ArticleX): Long

    @Delete
    suspend fun deleteNews(articleX: ArticleX)

}