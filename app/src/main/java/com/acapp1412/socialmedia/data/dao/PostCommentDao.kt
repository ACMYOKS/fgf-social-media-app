package com.acapp1412.socialmedia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.acapp1412.socialmedia.data.model.PostComment

@Dao
abstract class PostCommentDao {
    @Query("SELECT * FROM post_comment WHERE postId = :postId")
    abstract suspend fun getComments(postId: Int): List<PostComment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertComments(comments: List<PostComment>)
}
