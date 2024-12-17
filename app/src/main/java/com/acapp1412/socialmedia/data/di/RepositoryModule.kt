package com.acapp1412.socialmedia.data.di

import com.acapp1412.socialmedia.data.OnlineFirstPostCommentRepository
import com.acapp1412.socialmedia.data.PostRepository
import com.acapp1412.socialmedia.data.OnlineFirstPostRepository
import com.acapp1412.socialmedia.data.PostCommentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPostRepository(impl: OnlineFirstPostRepository): PostRepository

    @Binds
    abstract fun bindPostCommentRepository(impl: OnlineFirstPostCommentRepository): PostCommentRepository
}