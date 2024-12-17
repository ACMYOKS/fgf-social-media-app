package com.acapp1412.socialmedia.ui.feature

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.acapp1412.socialmedia.R
import com.acapp1412.socialmedia.data.model.Post
import com.acapp1412.socialmedia.ui.theme.SocialMediaTheme
import com.acapp1412.socialmedia.ui.toTimeString

@Composable
fun PostListScreen(
    modifier: Modifier = Modifier,
    viewModel: PostListViewModel = hiltViewModel(),
    onClickPost: (Post) -> Unit = {}
) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val hasMoreToLoad by viewModel.hasMoreToLoad.collectAsStateWithLifecycle()

    PostListScreen(
        modifier = modifier,
        posts = posts,
        refreshing = refreshing,
        onRefresh = viewModel::refreshPosts,
        hasMoreToLoad = hasMoreToLoad,
        onLoadMore = viewModel::loadMorePosts,
        onClickPost = onClickPost
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListScreen(
    modifier: Modifier = Modifier,
    posts: List<Post>,
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    hasMoreToLoad: Boolean = false,
    onLoadMore: () -> Unit = {},
    onClickPost: (Post) -> Unit = {}
) {
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh)
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        val lazyListState = rememberLazyListState()
        val endOfListReached by remember { derivedStateOf { lazyListState.isScrolledToTheEnd() } }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!refreshing) {
                items(posts, key = { it.id }) {
                    PostAbstractCell(it, onClickPost = onClickPost)
                }
                item {
                    BottomLoadingIndicator(hasMoreToLoad)
                }
            }
        }

        // load more item when end of list reached
        LaunchedEffect(endOfListReached) {
            if (endOfListReached && hasMoreToLoad && !refreshing) {
                onLoadMore()
                Log.d("momo", "on load more")
            }
        }

        Surface(
            modifier =
            Modifier
                .size(40.dp)
                .align(Alignment.TopCenter)
                .pullRefreshIndicatorTransform(pullRefreshState),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.inversePrimary,
            shadowElevation = if (pullRefreshState.progress > 0 || refreshing) 20.dp else 0.dp,
        ) {
            Box {
                if (refreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(25.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}

@Composable
fun BottomLoadingIndicator(
    hasMoreToLoad: Boolean,
    modifier: Modifier = Modifier
) {
    if (hasMoreToLoad) {
        CircularProgressIndicator(
            modifier = modifier
                .size(50.dp)
                .padding(8.dp)
        )
    } else {
        Text(
            text = "No more posts",
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun LazyListState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun PostAbstractCell(
    post: Post,
    modifier: Modifier = Modifier,
    onClickPost: (Post) -> Unit = {}
) {
    Box(
        modifier = modifier
            .clickable { onClickPost(post) }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )
            Column(
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                Text(
                    text = post.title,
                    modifier = Modifier.width(IntrinsicSize.Max),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = post.content,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .paddingFromBaseline(top = 18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = post.createdAt.toTimeString(),
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .paddingFromBaseline(top = 18.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
fun PostListScreenPreview() {
    SocialMediaTheme {
        PostListScreen(
            modifier = Modifier.fillMaxSize(),
            posts = ExamplePosts,
        )
    }
}

@Preview
@Composable
fun BottomLoadingIndicatorTruePreview() {
    SocialMediaTheme {
        BottomLoadingIndicator(hasMoreToLoad = true)
    }
}

@Preview
@Composable
fun BottomLoadingIndicatorFalsePreview() {
    SocialMediaTheme {
        BottomLoadingIndicator(hasMoreToLoad = false)
    }
}

@Preview
@Composable
fun PostAbstractCellPreview() {
    SocialMediaTheme {
        PostAbstractCell(ExamplePosts[0], onClickPost = {})
    }
}

private val ExamplePosts = listOf(
    Post(1, "url", "title1", "content1", false, 0L),
    Post(2, "url", "title2", "content2", false, 0L),
)