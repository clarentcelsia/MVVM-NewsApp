package com.example.newsapp.news

// Get News Data
data class Article(
    val articles: MutableList<ArticleX>,
    val status: String,
    val totalResults: Int
)