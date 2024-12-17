package com.acapp1412.socialmedia.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

typealias RemotePost = com.acapp1412.socialmedia.data.remote.model.Post
typealias RemotePostPage = com.acapp1412.socialmedia.data.remote.model.PostPage

@Entity("post")
data class Post(
    @PrimaryKey val id: Int,
    @ColumnInfo("image_url") val imageUrl: String,
    val title: String,
    val content: String,
    val liked: Boolean,
    @ColumnInfo("created_at")
    val createdAt: Long
)


fun Post.toExternalModel() = RemotePost(id, imageUrl, title, content, liked, createdAt)
fun RemotePost.toEntity() = Post(id, imageUrl, title, content, liked, createdAt)

@Entity("post_page", primaryKeys = ["page", "page_size"])
data class PostPage(
    val page: Int,
    @ColumnInfo("page_size") val pageSize: Int,
    val total: Int,
    @ColumnInfo("end_of_page") val endOfPage: Boolean
)

fun PostPage.toExternalModel() = RemotePostPage(page, pageSize, emptyList(), total, endOfPage)
fun RemotePostPage.toEntity() = PostPage(page, pageSize, total, endOfPage)