package com.example.ch09.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// TODO [T4.3] NewsDatabase singleton dengan version = 1 & exportSchema = true
@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "kabar_kini.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}