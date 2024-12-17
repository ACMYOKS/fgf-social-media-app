package com.acapp1412.socialmedia.data.di

import android.content.Context
import com.acapp1412.socialmedia.data.remote.InMemoryDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    @Singleton
    fun provideInMemoryDataStore(@ApplicationContext context: Context): InMemoryDataStore {
        return InMemoryDataStore(context)
    }
}