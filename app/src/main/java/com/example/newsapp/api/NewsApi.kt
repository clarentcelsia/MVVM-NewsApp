package com.example.newsapp.api

import com.example.newsapp.news.Article
import com.example.newsapp.util.Common.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getLatestNews(
        @Query("sources")
        sources: String = "bbc-news",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY

    ) : Response<Article>

    //https://newsapi.org/docs/endpoints/everything
    @GET("v2/everything")
    suspend fun getSearchNews(
        @Query("q")
        query: String = "",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY

    ): Response<Article>

}

