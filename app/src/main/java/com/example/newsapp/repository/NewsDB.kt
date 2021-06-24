package com.example.newsapp.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.news.ArticleX
import com.example.newsapp.news.Source

@Database(entities = [ArticleX::class], version = 1)
@TypeConverters(SourceConverter::class)
abstract class NewsDB: RoomDatabase() {

    // to issue queries to its db
    abstract fun getNewsDAO(): NewsDAO

    // create db
    companion object{
        @Volatile
        private var INSTANCE: NewsDB? = null
        private val LOCK = Any()

        private fun createDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NewsDB::class.java,
                "dbNews"
        ).build()

        operator fun invoke(mCTX: Context) = INSTANCE?: synchronized(LOCK){
            INSTANCE?: createDB(mCTX).also { INSTANCE = it }
        }
    }
}