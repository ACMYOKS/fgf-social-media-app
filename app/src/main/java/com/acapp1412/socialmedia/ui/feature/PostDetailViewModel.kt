package com.acapp1412.socialmedia.ui.feature

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.acapp1412.socialmedia.data.PostCommentRepository
import com.acapp1412.socialmedia.data.PostRepository
import com.acapp1412.socialmedia.data.model.Post
import com.acapp1412.socialmedia.data.model.PostComment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val postCommentRepository: PostCommentRepository
) : ViewModel() {
    private val postId = savedStateHandle.toRoute<NavRoute.PostItem>().postId

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _commentUiState = MutableStateFlow<CommentUiState>(CommentUiState.Loading)
    val commentUiState = _commentUiState.asStateFlow()

    init {
        viewModelScope.launch {
            getPost(postId)
            getComments(postId)
        }
    }

    private suspend fun getPost(postId: Int) {
        val post = postRepository.getPost(postId)
        if (post == null) {
            _uiState.value = UiState.Error
        } else {
            _uiState.value = UiState.Success(post)
        }
    }

    private suspend fun getComments(postId: Int) {
        val comments = postCommentRepository.getPostComments(postId)
        _commentUiState.value = CommentUiState.Success(comments)
    }

    fun updateLike(liked: Boolean) {
        viewModelScope.launch {
            postRepository.updateLike(postId, liked)
        }
    }

    fun addComment(comment: String) {
        viewModelScope.launch {
            postCommentRepository.addPostComment(postId, comment)
            getComments(postId)
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val post: Post) : UiState()
        data object Error : UiState()
    }

    sealed class CommentUiState {
        data object Loading : CommentUiState()
        data class Success(val comments: List<PostComment>) : CommentUiState()
    }

}