package com.example.newsapp.api

import com.example.newsapp.util.Common.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        // created once
        private val retrofit by lazy {

            // Connect to -> HttpServer
            val logInter = HttpLoggingInterceptor()
            logInter.setLevel(HttpLoggingInterceptor.Level.BODY)

            //Client - logInter(connector) -> Server
            val client = OkHttpClient.Builder()
                .addInterceptor(logInter)
                .build()

            //Pass Client to Instance
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }

        //api = retrofit instance
        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}