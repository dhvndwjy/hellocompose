package com.example.ch10.di

import com.example.ch10.FakeNewsRepository
import com.example.ch10.data.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object FakeRepositoryModule{

    @Provides
    @Singleton
    fun provideNewsRepository(): NewsRepository {
        return FakeNewsRepository()
    }
}