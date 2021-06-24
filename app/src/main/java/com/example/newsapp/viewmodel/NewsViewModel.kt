package com.example.newsapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.NewsApp
import com.example.newsapp.news.Article
import com.example.newsapp.news.ArticleX
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Response
import kotlinx.coroutines.launch
import okio.IOException

// UI-Repo
class NewsViewModel(
    val app: Application,
    private val repo: NewsRepository
) : AndroidViewModel(app) {

    val latestNews: MutableLiveData<Response<Article>> = MutableLiveData()
    val searchNews: MutableLiveData<Response<Article>> = MutableLiveData()
    var latestNewsResponse: Article? = null
    var searchNewsResponse: Article? = null
    var initLatestNewsPage = 1
    var initSearchNewsPage = 1

    init {
        getLatestNews("bbc-news")
    }

    fun getLatestNews(source: String) = viewModelScope.launch {
        safeNewsCall(source)
    }

    fun saveBookmarkedNews(articleX: ArticleX) = viewModelScope.launch {
        repo.saveBookmarkedNews(articleX)
    }

    fun getBookmarkedNews() = repo.getBookmarkedNews()

    fun deleteNews(articleX: ArticleX) = viewModelScope.launch {
        repo.deleteNews(articleX)
    }

    fun getSearchedNews(query: String) = viewModelScope.launch {
        safeSearchCall(query)
    }

    private suspend fun safeSearchCall(query:String){
        searchNews.postValue(Response.Loading())
        try {
            if(hasConnection()){
                val responses = repo.getSearchNews(query, initSearchNewsPage)
                searchNews.postValue(handleSearchResponses(responses))
            }else
                searchNews.postValue(Response.Error("NO INTERNET CONNECTION!"))
        }catch (t: Throwable){
            when(t){
                is IOException -> latestNews.postValue(Response.Error("NETWORK FAILURE!"))
                else -> latestNews.postValue(Response.Error("CONVERSION ERROR!"))
            }
        }
    }

    private suspend fun safeNewsCall(source:String){
        latestNews.postValue(Response.Loading())
        try{
            if(hasConnection()){
                val response = repo.getLatestNews(source, initLatestNewsPage)
                latestNews.postValue(handleResponses(response))
            }else
                latestNews.postValue(Response.Error("NO INTERNET CONNECTION!"))
        }catch (t: Throwable){
            when(t){
                is IOException -> latestNews.postValue(Response.Error("NETWORK FAILURE!"))
                else -> latestNews.postValue(Response.Error("CONVERSION ERROR!"))
            }
        }
    }

    // handle response from retrofit
    private fun handleResponses(response: retrofit2.Response<Article>): Response<Article>{
        if(response.isSuccessful){
            response.body()?.let { result->
                initLatestNewsPage++
                if(latestNewsResponse==null) latestNewsResponse = result
                else{
                    val oldNews = latestNewsResponse?.articles
                    val currentNews = result.articles
                    oldNews?.addAll(currentNews)
                }
                return Response.Success(latestNewsResponse?: result)
            }
        }
        return Response.Error(response.message())
    }

    // handle response from retrofit
    private fun handleSearchResponses(response: retrofit2.Response<Article>): Response<Article>{
        if(response.isSuccessful){
            response.body()?.let { result->
                initSearchNewsPage++
                if(searchNewsResponse==null) searchNewsResponse = result
                else{
                    val oldNews = searchNewsResponse?.articles
                    val currentNews = result.articles
                    oldNews?.addAll(currentNews)
                }
                return Response.Success(searchNewsResponse?: result)
            }
        }
        return Response.Error(response.message())
    }
    @Suppress("DEPRECATION")
    private fun hasConnection(): Boolean{
        val connManager = getApplication<NewsApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val active = connManager.activeNetwork?: return false
            val capabilities = connManager.getNetworkCapabilities(active)?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }else{
            connManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
        return false
    }
}