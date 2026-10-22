package com.example.ch10.di

import com.example.ch10.BuildConfig
import com.example.ch10.data.remote.NewsApiService
import com.example.ch10.data.remote.createNewsApiService
import com.example.ch10.data.remote.createOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = createOkHttpClient(
        apiKey = BuildConfig.NEWS_API_KEY,
        enableLogging = BuildConfig.DEBUG
    )

    @Provides
    @Singleton
    fun provideNewsApiService(
        okHttpClient: OkHttpClient,
        json: Json
    ): NewsApiService = createNewsApiService(
        baseUrl = "https://newsapi.org/v2/",
        client = okHttpClient,
        json = json
    )
}