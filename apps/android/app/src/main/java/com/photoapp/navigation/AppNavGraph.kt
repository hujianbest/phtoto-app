package com.photoapp.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.photoapp.auth.AuthViewModel
import com.photoapp.feed.FeedScreen
import com.photoapp.profile.ProfileScreen
import com.photoapp.post.CreatePostScreen
import com.photoapp.post.PostRepository
import com.photoapp.review.ReviewSheet

private const val ROUTE_LOGIN = "login"
private const val ROUTE_DISCOVER = "discover"
private const val ROUTE_CREATE_POST = "create_post"
private const val ROUTE_REVIEW = "review"
private const val ROUTE_PROFILE = "profile"

@Composable
fun AppNavGraph(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(uiState.isLoggedIn, currentRoute) {
        // Only redirect from login route after auth.
        if (uiState.isLoggedIn && (currentRoute == null || currentRoute == ROUTE_LOGIN)) {
            navController.navigate(ROUTE_DISCOVER) {
                popUpTo(ROUTE_LOGIN) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(onLogin = authViewModel::login)
        }
        composable(ROUTE_DISCOVER) {
            FeedScreen(
                posts = PostRepository.posts,
                onOpenCreatePost = { navController.navigate(ROUTE_CREATE_POST) },
                onOpenReviewSheet = { navController.navigate(ROUTE_REVIEW) },
                onOpenProfile = { navController.navigate(ROUTE_PROFILE) }
            )
        }
        composable(ROUTE_CREATE_POST) {
            CreatePostScreen(
                onPublish = { title, intent, imageUrl, exifSummary, authorName ->
                    PostRepository.publish(
                        title = title,
                        intent = intent,
                        imageUrl = imageUrl,
                        exifSummary = exifSummary,
                        authorName = authorName
                    )
                    navController.popBackStack()
                }
            )
        }
        composable(ROUTE_REVIEW) {
            ReviewSheet(onBack = { navController.popBackStack() })
        }
        composable(ROUTE_PROFILE) {
            ProfileScreen(
                posts = PostRepository.posts,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "登录页")
        Button(
            onClick = onLogin,
            modifier = Modifier.testTag("login_button")
        ) {
            Text(text = "登录")
        }
    }
}
