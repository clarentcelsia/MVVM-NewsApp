package com.example.newsapp.util

sealed class Response<T>(
    val data: T? = null,
    val message: String?=null
) {
    class Success<T>(mData: T): Response<T>(mData)
    class Loading<T>: Response<T>()
    class Error<T>(mMessage: String, mData:T?=null): Response<T>(mData, mMessage)
}