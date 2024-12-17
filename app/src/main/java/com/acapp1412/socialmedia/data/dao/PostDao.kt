package com.acapp1412.socialmedia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acapp1412.socialmedia.data.model.Post
import com.acapp1412.socialmedia.data.model.PostPage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PostDao {
    @Query("SELECT * FROM post ORDER BY id ASC LIMIT :pageSize OFFSET :page * :pageSize")
    abstract suspend fun getPosts(page: Int, pageSize: Int): List<Post>

    @Query("UPDATE post SET liked = :liked WHERE id = :postId")
    abstract suspend fun updateLike(postId: Int, liked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPosts(post: List<Post>)

    @Query("SELECT * FROM post_page WHERE page = :page AND page_size = :pageSize")
    abstract suspend fun getPostPage(page: Int, pageSize: Int): PostPage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPostPage(postPage: PostPage)

    @Query("SELECT * FROM post WHERE id = :postId")
    abstract suspend fun getPost(postId: Int): Post?
}