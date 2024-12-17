package com.acapp1412.socialmedia.data.di

import com.acapp1412.socialmedia.data.remote.InMemoryDataStore
import com.acapp1412.socialmedia.data.remote.RemoteApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteApiModule {
    @Provides
    @Singleton
    fun provideRemoteApi(inMemoryDataStore: InMemoryDataStore): RemoteApi {
        return RemoteApi(inMemoryDataStore)
    }
}