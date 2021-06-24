package com.example.newsapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.repository.NewsRepository
import java.lang.IllegalArgumentException

class NewsViewModelFactory(
    val app: Application,
    private val newsRepository: NewsRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewsViewModel::class.java)){
            return NewsViewModel(app, newsRepository) as T
        }
        throw IllegalArgumentException("unknown viewmodel class")

    }
}