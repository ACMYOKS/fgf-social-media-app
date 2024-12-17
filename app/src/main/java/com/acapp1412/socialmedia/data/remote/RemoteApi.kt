package com.acapp1412.socialmedia.data.remote

import com.acapp1412.socialmedia.data.remote.model.ActionResult
import com.acapp1412.socialmedia.data.remote.model.Post
import com.acapp1412.socialmedia.data.remote.model.PostComment
import com.acapp1412.socialmedia.data.remote.model.PostPage
import com.acapp1412.socialmedia.data.remote.model.Response
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * Fake remote data source. Each call costs 2 seconds.
 */
class RemoteApi @Inject constructor(
    private val inMemoryDataStore: InMemoryDataStore,
) {
    suspend fun getPosts(page: Int, pageSize: Int): Response<PostPage> {
        delay(2.seconds)
        return Response(success = true, inMemoryDataStore.getPosts(page, pageSize))
    }

    suspend fun getComments(postId: Int): Response<List<PostComment>> {
        delay(2.seconds)
        return Response(success = true, inMemoryDataStore.getComments(postId))
    }

    suspend fun insertComment(postId: Int, comment: String): Response<PostComment> {
        delay(2.seconds)
        val newComment = inMemoryDataStore.insertComments(postId, comment)
        return Response(success = newComment != null, data = newComment)
    }

    suspend fun updatePostLiked(postId: Int, liked: Boolean): Response<ActionResult> {
        delay(2.seconds)
        val success = inMemoryDataStore.updatePostLiked(postId, liked)
        return Response(success = success, data = ActionResult(success))
    }

    suspend fun getPost(postId: Int): Response<Post> {
        delay(2.seconds)
        val post = inMemoryDataStore.getPost(postId)
        return Response(success = post != null, data = post)
    }
}