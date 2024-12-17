package com.acapp1412.socialmedia.ui.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acapp1412.socialmedia.data.PostRepository
import com.acapp1412.socialmedia.data.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
    private var currentPage = 0
    private val _hasMoreToLoad = MutableStateFlow(false)
    val hasMoreToLoad = _hasMoreToLoad.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _loadingMore = MutableStateFlow(false)
    val loadingMore = _loadingMore.asStateFlow()

    init {
        viewModelScope.launch {
            delay(100.milliseconds)
            refreshPosts()
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            resetPageInfo()

            _refreshing.value = true
            val result = postRepository.getPosts(currentPage, 10)
            _hasMoreToLoad.value = result.first?.endOfPage?.not() ?: false
            _posts.value = result.second
            _refreshing.value = false
        }
    }

    private fun resetPageInfo() {
        currentPage = 0
        _hasMoreToLoad.value = false
    }

    fun loadMorePosts() {
        if (!_hasMoreToLoad.value) return
        viewModelScope.launch {
            currentPage++

            _loadingMore.value = true
            val result = postRepository.getPosts(currentPage, 10)
            _hasMoreToLoad.value = result.first?.endOfPage?.not() ?: false
            _posts.getAndUpdate {
                it + result.second
            }
            _loadingMore.value = false
        }
    }
}