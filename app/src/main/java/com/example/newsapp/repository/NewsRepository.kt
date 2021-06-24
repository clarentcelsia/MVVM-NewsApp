package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.news.ArticleX

// repo(getDB) <-> dao(commmand)
class NewsRepository(val newsDB: NewsDB ) {

    //get from web
    suspend fun getLatestNews(sources:String, pageNumber:Int) = RetrofitInstance.api.getLatestNews(sources, pageNumber)

    //save to db
    suspend fun saveBookmarkedNews(articleX: ArticleX) = newsDB.getNewsDAO().insertBookmarkedNews(articleX)

    //get from db
    fun getBookmarkedNews() = newsDB.getNewsDAO().getArticles()

    suspend fun deleteNews(articleX: ArticleX) = newsDB.getNewsDAO().deleteNews(articleX)

    suspend fun getSearchNews(query: String, pageNumber: Int) = RetrofitInstance.api.getSearchNews(query, pageNumber)
}