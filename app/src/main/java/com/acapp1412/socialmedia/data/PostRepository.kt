package com.acapp1412.socialmedia.data

import com.acapp1412.socialmedia.data.dao.PostDao
import com.acapp1412.socialmedia.data.model.Post
import com.acapp1412.socialmedia.data.model.PostComment
import com.acapp1412.socialmedia.data.model.PostPage
import com.acapp1412.socialmedia.data.model.RemotePostPage
import com.acapp1412.socialmedia.data.model.toEntity
import com.acapp1412.socialmedia.data.remote.RemoteApi
import com.acapp1412.socialmedia.network.NetworkMonitor
import com.acapp1412.socialmedia.network.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/***
 * Repository for [Post]
 */
interface PostRepository {
    suspend fun getPosts(page: Int, pageSize: Int): Pair<PostPage?, List<Post>>
    suspend fun getPost(postId: Int): Post?
    suspend fun updateLike(postId: Int, liked: Boolean)
}

/***
 * Repository for [Post] that takes both remote and local data sources, but always try to
 * get the remote data first. It monitors the network connectivity with [NetworkMonitor], and get
 * the remote data only when the network is online. Once the data is fetched from the remote, it
 * stores the data to local database for local access. If the app is offline, it will only get the
 * local resource.
 */
class OnlineFirstPostRepository @Inject constructor(
    private val postDao: PostDao,
    private val remoteApi: RemoteApi,
    networkMonitor: NetworkMonitor,
    @ApplicationScope private val applicationScope: CoroutineScope
) : PostRepository {
    private val isOnline = networkMonitor.isOnline.stateIn(
        applicationScope,
        SharingStarted.Eagerly,
        false
    )

    /**
     * function to return posts [Post] with paging information [PostPage] as a pair.
     */
    override suspend fun getPosts(page: Int, pageSize: Int): Pair<PostPage?, List<Post>> {
        if (isOnline.value) {
            val posts = remoteApi.getPosts(page, pageSize)
            // assume no network error
            // update local db
            insertPost(posts.data!!)
        }
        return Pair(
            postDao.getPostPage(page, pageSize),
            postDao.getPosts(page, pageSize)
        )
    }

    /**
     * function to return a post [Post] by its id.
     */
    override suspend fun getPost(postId: Int): Post? {
        if (isOnline.value) {
            val post = remoteApi.getPost(postId)
            post.data?.let { postDao.insertPosts(listOf(it.toEntity())) }
        }
        // update local db
        return postDao.getPost(postId)
    }

    /**
     * function to update the like status of a post [Post].
     */
    override suspend fun updateLike(postId: Int, liked: Boolean) {
        if (isOnline.value) {
            remoteApi.updatePostLiked(postId, liked)
        }
        // update local db
        postDao.updateLike(postId, liked)
    }

    /**
     * function to update local db with the given [RemotePostPage].
     */
    private suspend fun insertPost(postPage: RemotePostPage) {
        postDao.insertPostPage(postPage.toEntity())
        postDao.insertPosts(postPage.posts.map { it.toEntity() })
    }
}