package com.example.newsapp.repository

import androidx.room.TypeConverter
import com.example.newsapp.news.Source

class SourceConverter {
    @TypeConverter
    fun fromSource(source: Source) : String {
        return source.name //get source.name
    }

    //convert name String to Source class
    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}