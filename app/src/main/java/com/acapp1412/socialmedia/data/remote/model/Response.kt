package com.acapp1412.socialmedia.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val success: Boolean,
    val data: T?
)

@Serializable
data class Post(
    val id: Int,
    val imageUrl: String,
    val title: String,
    val content: String,
    val liked: Boolean,
    val createdAt: Long
)

@Serializable
data class PostPage(
    val page: Int,
    val pageSize: Int,
    val posts: List<Post>,
    val total: Int,
    val endOfPage: Boolean
)

@Serializable
data class PostComment(
    val commentId: Int,
    val postId: Int,
    val comment: String,
    val createdAt: Long
)

@Serializable
data class ActionResult(val success: Boolean)