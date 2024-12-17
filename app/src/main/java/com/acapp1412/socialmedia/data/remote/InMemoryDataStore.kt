package com.acapp1412.socialmedia.data.remote

import android.content.Context
import com.acapp1412.socialmedia.R
import com.acapp1412.socialmedia.data.remote.model.Post
import com.acapp1412.socialmedia.data.remote.model.PostComment
import com.acapp1412.socialmedia.data.remote.model.PostPage
import com.acapp1412.socialmedia.data.remote.model.Response
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val PostsFileName = "posts.json"
private const val CommentsFileName = "comments.json"

/**
 * Underlying in-memory data store for fake data source.
 * It reads the data from the raw resources post_data_source as the default list of posts, and any
 * updates to the dataset in the subsequent calls will be written to the file corresponding to the
 * collection.
 */
class InMemoryDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val posts = mutableListOf<Post>()
    private val comments = mutableListOf<PostComment>()

    init {
        initPosts()
        initComments()
    }

    fun getPosts(page: Int, pageSize: Int): PostPage {
        val start = page * pageSize
        val end = (page + 1) * pageSize
        val subList = if (start > posts.size - 1) {
            emptyList()
        } else {
            posts.subList(start, end.coerceAtMost(posts.size))
        }
        return PostPage(
            page = page,
            pageSize = pageSize,
            posts = subList,
            total = posts.size,
            endOfPage = page * pageSize >= posts.size
        )
    }

    fun updatePostLiked(postId: Int, liked: Boolean): Boolean {
        val idx = posts.indexOfFirst { it.id == postId }
        if (idx >= 0) {
            posts[idx] = posts[idx].copy(liked = liked)
            writePosts(posts)
            return true
        }
        return false
    }

    fun getComments(postId: Int): List<PostComment> {
        return comments.filter { it.postId == postId }
    }

    fun insertComments(postId: Int, comment: String): PostComment? {
        val idx = posts.indexOfFirst { it.id == postId }
        if (idx >= 0) {
            val newComment = PostComment(comments.size, postId, comment, System.currentTimeMillis())
            comments.add(newComment)
            writeComments(comments)
            return newComment
        }
        return null
    }

    fun getPost(postId: Int): Post? {
        return posts.find { it.id == postId }
    }

    private fun initPosts() {
        val posts = readJsonFromFile<Response<List<Post>>>(PostsFileName)
        if (posts?.data.isNullOrEmpty()) {
            this.posts.addAll(getInitPosts(R.raw.post_data_source))
            writePosts(this.posts)
        } else {
            this.posts.addAll(posts?.data.orEmpty())
        }
    }

    private fun initComments() {
        val comments = readJsonFromFile<Response<List<PostComment>>>(CommentsFileName)
        this.comments.addAll(comments?.data.orEmpty())
    }

    private inline fun <reified T> readJsonFromFile(filename: String): T? {
        try {
            val json = context.openFileInput(filename).bufferedReader().use { it.readText() }
            return Json.decodeFromString(json)
        } catch (e: Exception) {
            return null
        }
    }

    private fun getInitPosts(resourceId: Int): List<Post> {
        val response = Json.decodeFromString<Response<List<Post>>>(readJson(resourceId))
        return response.data!!
    }

    private fun readJson(resourceId: Int): String {
        return context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
    }

    private fun writePosts(posts: List<Post>) {
        val json = Json.encodeToString(posts)
        context.openFileOutput(PostsFileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    private fun writeComments(comments: List<PostComment>) {
        val json = Json.encodeToString(comments)
        context.openFileOutput(CommentsFileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }
}