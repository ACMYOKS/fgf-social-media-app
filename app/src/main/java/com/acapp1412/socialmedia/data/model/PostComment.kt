package com.acapp1412.socialmedia.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

typealias RemotePostComment = com.acapp1412.socialmedia.data.remote.model.PostComment

@Entity("post_comment")
data class PostComment(
    @PrimaryKey val commentId: Int,
    val postId: Int,
    val comment: String,
    @ColumnInfo("created_at") val createdAt: Long
)

fun PostComment.toExternalModel() = RemotePostComment(commentId, postId, comment, createdAt)
fun RemotePostComment.toEntity() = PostComment(commentId, postId, comment, createdAt)