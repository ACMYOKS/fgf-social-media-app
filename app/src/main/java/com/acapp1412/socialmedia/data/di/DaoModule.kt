package com.acapp1412.socialmedia.data.di

import com.acapp1412.socialmedia.data.AppDatabase
import com.acapp1412.socialmedia.data.dao.PostCommentDao
import com.acapp1412.socialmedia.data.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }

    @Provides
    fun providePostCommentDao(appDatabase: AppDatabase): PostCommentDao {
        return appDatabase.postCommentDao()
    }


}