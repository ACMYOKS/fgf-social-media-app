package com.acapp1412.socialmedia.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acapp1412.socialmedia.data.dao.PostCommentDao
import com.acapp1412.socialmedia.data.dao.PostDao
import com.acapp1412.socialmedia.data.model.Post
import com.acapp1412.socialmedia.data.model.PostComment
import com.acapp1412.socialmedia.data.model.PostPage

@Database(entities = [Post::class, PostComment::class, PostPage::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postCommentDao(): PostCommentDao
}