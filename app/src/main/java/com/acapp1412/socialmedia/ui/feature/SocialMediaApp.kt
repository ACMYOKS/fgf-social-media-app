package com.acapp1412.socialmedia.ui.feature

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

sealed class NavRoute {
    @Serializable
    data object PostList : NavRoute()
    @Serializable
    data class PostItem(val postId: Int) : NavRoute()
}

@Composable
fun SocialMediaApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoute.PostList) {
        composable<NavRoute.PostList> {
            PostListScreen(
                modifier = modifier,
                onClickPost = {
                    navController.navigate(NavRoute.PostItem(it.id))
                }
            )
        }
        composable<NavRoute.PostItem> {
            PostDetailScreen(modifier = modifier)
        }
    }
}