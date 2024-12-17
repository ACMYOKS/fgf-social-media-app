package com.acapp1412.socialmedia.data

import com.acapp1412.socialmedia.data.dao.PostCommentDao
import com.acapp1412.socialmedia.data.model.PostComment
import com.acapp1412.socialmedia.data.model.toEntity
import com.acapp1412.socialmedia.data.remote.RemoteApi
import com.acapp1412.socialmedia.network.NetworkMonitor
import com.acapp1412.socialmedia.network.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/***
 * Repository for [PostComment]
 */
interface PostCommentRepository {
    suspend fun getPostComments(postId: Int): List<PostComment>
    suspend fun addPostComment(postId: Int, content: String)
}

/***
 * Repository for [PostComment] that takes both remote and local data sources, but always try to
 * get the remote data first. It monitors the network connectivity with [NetworkMonitor], and get
 * the remote data only when the network is online. Once the data is fetched from the remote, it
 * stores the data to local database for local access. If the app is offline, it will only get the
 * local resource.
 */
class OnlineFirstPostCommentRepository @Inject constructor(
    private val postCommentDao: PostCommentDao,
    private val remoteApi: RemoteApi,
    networkMonitor: NetworkMonitor,
    @ApplicationScope private val applicationScope: CoroutineScope
) : PostCommentRepository {
    private val isOnline = networkMonitor.isOnline.stateIn(
        applicationScope,
        SharingStarted.Eagerly,
        false
    )

    /**
     * function to return comments [PostComment] of a post with the given [postId].
     */
    override suspend fun getPostComments(postId: Int): List<PostComment> {
        if (isOnline.value) {
            val comments = remoteApi.getComments(postId)
            insertComments(comments.data!!.map { it.toEntity() })
        }
        return postCommentDao.getComments(postId)
    }

    /**
     * function to update local db with the given [PostComment].
     */
    private suspend fun insertComments(comments: List<PostComment>) {
        postCommentDao.insertComments(comments)
    }

    /**
     * function to add a comment [PostComment] to a post [Post] with the given [postId].
     */
    override suspend fun addPostComment(postId: Int, content: String) {
        if (isOnline.value) {
            val comment = remoteApi.insertComment(postId, content)
            insertComments(listOf(comment.data!!.toEntity()))
        }
    }

}