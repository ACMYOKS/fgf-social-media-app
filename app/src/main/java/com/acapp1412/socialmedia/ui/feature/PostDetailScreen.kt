package com.acapp1412.socialmedia.ui.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.acapp1412.socialmedia.R
import com.acapp1412.socialmedia.data.model.Post
import com.acapp1412.socialmedia.data.model.PostComment
import com.acapp1412.socialmedia.ui.theme.SocialMediaTheme
import com.acapp1412.socialmedia.ui.toTimeString

@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val commentUiState by viewModel.commentUiState.collectAsStateWithLifecycle()
    PostDetailScreen(
        uiState = uiState,
        commentUiState = commentUiState,
        modifier = modifier,
        onNewLikeState = viewModel::updateLike,
        onNewComment = viewModel::addComment
    )
}

@Composable
fun PostDetailScreen(
    uiState: PostDetailViewModel.UiState,
    commentUiState: PostDetailViewModel.CommentUiState,
    modifier: Modifier = Modifier,
    onNewLikeState: (Boolean) -> Unit = {},
    onNewComment: (String) -> Unit = {}
) {
    when (uiState) {
        is PostDetailViewModel.UiState.Error -> ErrorScreen(modifier)
        is PostDetailViewModel.UiState.Loading -> LoadingScreen(modifier)
        is PostDetailViewModel.UiState.Success -> PostDetailContentWithComments(
            uiState.post,
            commentUiState = commentUiState,
            modifier = modifier,
            onNewLikeState = onNewLikeState,
            onNewComment = onNewComment
        )
    }
}

@Composable
fun PostDetailContentWithComments(
    post: Post,
    commentUiState: PostDetailViewModel.CommentUiState,
    modifier: Modifier = Modifier,
    // notify view model of the update of the like status
    onNewLikeState: (Boolean) -> Unit = {},
    // notify view model of the newly created comment
    onNewComment: (String) -> Unit = {}
) {
    var liked by rememberSaveable { mutableStateOf(post.liked) }
    var newComment by rememberSaveable { mutableStateOf("") }
    var comments by remember(commentUiState) {
        if (commentUiState is PostDetailViewModel.CommentUiState.Success) {
            mutableStateOf(commentUiState.comments)
        } else {
            mutableStateOf(emptyList())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                PostContent(post, Modifier.fillMaxWidth())
            }
            item {
                Text(
                    stringResource(R.string.comments),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            when (commentUiState) {
                is PostDetailViewModel.CommentUiState.Loading -> {
                    item {
                        PostCommentLoading(Modifier.fillMaxWidth())
                    }
                }

                is PostDetailViewModel.CommentUiState.Success -> {
                    if (comments.isEmpty()) {
                        item {
                            PostCommentEmpty(Modifier.fillMaxWidth())
                        }
                    } else {
                        items(comments, key = { it.commentId }) {
                            CommentCell(
                                comment = it,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                newComment,
                onValueChange = {
                    newComment = it
                },
                placeholder = { Text(stringResource(R.string.add_a_comment)) },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                onNewComment(newComment)
                                // Create an illusion of local update, new comment may not be
                                // updated to server; also the commentId should come from server,
                                // not from local. It is error prone since server implementation
                                // may change.
                                comments = comments + PostComment(
                                    comments.lastOrNull()?.let { it.commentId + 1 } ?: 0,
                                    post.id,
                                    newComment,
                                    System.currentTimeMillis()
                                )
                                newComment = ""
                            }
                        },
                        modifier = Modifier
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "send")
                    }
                }
            )
            OutlinedIconToggleButton(
                checked = liked,
                onCheckedChange = {
                    liked = it
                    onNewLikeState(it)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(60.dp),
            ) {
                if (liked) {
                    Icon(Icons.Filled.ThumbUp, contentDescription = "like")
                } else {
                    Icon(Icons.Outlined.ThumbUp, contentDescription = "like")
                }
            }
        }
    }
}

@Composable
private fun CommentCell(
    comment: PostComment,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = comment.comment, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.created_at, comment.createdAt.toTimeString()),
            style = MaterialTheme.typography.bodySmall
        )
    }

}

@Composable
private fun PostContent(post: Post, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        )
        Text(
            text = post.title,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.created_at, post.createdAt.toTimeString()),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = post.content,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PostCommentLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.loading_comments),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun PostCommentEmpty(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.no_comment_yet_be_the_first),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(25.dp),
            color = MaterialTheme.colorScheme.inversePrimary,
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.cannot_retrieve_post))
    }
}

@Preview
@Composable
fun PostDetailScreenPreview() {
    SocialMediaTheme {
        PostDetailScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = PostDetailViewModel.UiState.Success(ExamplePost),
            commentUiState = PostDetailViewModel.CommentUiState.Loading
        )
    }
}

@Preview
@Composable
fun PostDetailContentPreview() {
    SocialMediaTheme {
        PostDetailContentWithComments(
            modifier = Modifier.fillMaxSize(),
            post = ExamplePost,
            commentUiState = PostDetailViewModel.CommentUiState.Loading
        )
    }
}

@Preview
@Composable
fun CommentCellPreview() {
    SocialMediaTheme {
        CommentCell(ExampleComment)
    }
}

private val ExamplePost = Post(1, "url", "title1", "content1", false, 0L)
private val ExampleComment = PostComment(1, 1, "comment1", 0L)