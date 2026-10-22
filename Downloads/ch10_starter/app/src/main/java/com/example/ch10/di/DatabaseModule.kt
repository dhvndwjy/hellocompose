package com.example.ch10.di

import android.content.Context
import androidx.room.Room
import com.example.ch10.data.local.ArticleDao
import com.example.ch10.data.local.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NewsDatabase {
        return Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "kabar_kini.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(
        database: NewsDatabase
    ): ArticleDao {
        return database.articleDao()
    }
}