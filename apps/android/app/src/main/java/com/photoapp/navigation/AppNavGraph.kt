package com.photoapp.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.photoapp.auth.AuthViewModel
import com.photoapp.challenge.WeeklyChallengeScreen
import com.photoapp.feed.FeedScreen
import com.photoapp.network.ApiClient
import com.photoapp.profile.ProfileScreen
import com.photoapp.post.CreatePostScreen
import com.photoapp.post.PostRepository
import com.photoapp.report.ReportScreen
import com.photoapp.review.ReviewSheet
import kotlinx.coroutines.launch

private const val ROUTE_LOGIN = "login"
private const val ROUTE_DISCOVER = "discover"
private const val ROUTE_CREATE_POST = "create_post"
private const val ROUTE_REVIEW = "review"
private const val ROUTE_PROFILE = "profile"
private const val ROUTE_CHALLENGE = "challenge"
private const val ROUTE_REPORT = "report"

@Composable
fun AppNavGraph(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var reportHint by remember { mutableStateOf<String?>(null) }
    var feedMode by remember { mutableStateOf(PostRepository.FeedMode.RECOMMENDED) }
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

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            PostRepository.syncFromRemote(mode = feedMode, viewerEmail = uiState.email)
        }
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onLogin = authViewModel::login
            )
        }
        composable(ROUTE_DISCOVER) {
            FeedScreen(
                posts = PostRepository.posts,
                onOpenCreatePost = { navController.navigate(ROUTE_CREATE_POST) },
                onOpenReviewSheet = { navController.navigate(ROUTE_REVIEW) },
                onOpenProfile = { navController.navigate(ROUTE_PROFILE) },
                onOpenChallenge = { navController.navigate(ROUTE_CHALLENGE) },
                onReportPost = { postId ->
                    navController.navigate("$ROUTE_REPORT/$postId")
                },
                onSelectRecommended = {
                    feedMode = PostRepository.FeedMode.RECOMMENDED
                    scope.launch {
                        PostRepository.syncFromRemote(mode = feedMode, viewerEmail = uiState.email)
                    }
                },
                onSelectFollowing = {
                    feedMode = PostRepository.FeedMode.FOLLOWING
                    scope.launch {
                        PostRepository.syncFromRemote(mode = feedMode, viewerEmail = uiState.email)
                    }
                },
                onFollowAuthor = { authorName ->
                    scope.launch {
                        runCatching {
                            if (uiState.email.isNotBlank() && authorName.contains("@")) {
                                ApiClient.followAuthor(
                                    followerEmail = uiState.email,
                                    followeeEmail = authorName
                                )
                            } else {
                                false
                            }
                        }
                    }
                },
                reportHint = reportHint
            )
        }
        composable(ROUTE_CREATE_POST) {
            CreatePostScreen(
                onPublish = { title, intent, imageUrl, exifSummary, authorName ->
                    scope.launch {
                        PostRepository.publish(
                            title = title,
                            intent = intent,
                            imageUrl = imageUrl,
                            exifSummary = exifSummary,
                            authorName = authorName,
                            viewerEmail = uiState.email
                        )
                        PostRepository.syncFromRemote(mode = feedMode, viewerEmail = uiState.email)
                    }
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
                email = uiState.email,
                challengeJoinedAt = uiState.challengeJoinedAt,
                reportHistory = uiState.reportHistory,
                onBack = { navController.popBackStack() }
            )
        }
        composable(ROUTE_CHALLENGE) {
            WeeklyChallengeScreen(
                joinedAt = uiState.challengeJoinedAt,
                onJoinChallenge = authViewModel::joinWeeklyChallenge,
                onGoCreatePost = { navController.navigate(ROUTE_CREATE_POST) },
                onBack = { navController.popBackStack() }
            )
        }
        composable("$ROUTE_REPORT/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId").orEmpty()
            ReportScreen(
                targetPostId = postId,
                onSubmit = { reason ->
                    scope.launch {
                        val success = runCatching {
                            ApiClient.createPostReport(
                                postId = postId,
                                reason = reason,
                                reporterEmail = uiState.email
                            )
                        }.getOrDefault(false)
                        authViewModel.addReportHistory(postId = postId, reason = reason)
                        if (success) {
                            authViewModel.syncReportHistoryFromRemote()
                        }
                        reportHint = if (success) {
                            "举报已提交，感谢反馈。"
                        } else {
                            "举报提交失败，已记录到本地会话。"
                        }
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (email: String, password: String) -> Unit
) {
    var email by remember { mutableStateOf("demo@photo.app") }
    var password by remember { mutableStateOf("demo12345") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "登录页")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("邮箱") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("login_email_input")
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("login_password_input")
        )
        Button(
            onClick = { onLogin(email, password) },
            enabled = !isLoading,
            modifier = Modifier
                .padding(top = 12.dp)
                .testTag("login_button")
        ) {
            Text(text = if (isLoading) "登录中..." else "登录")
        }
        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .testTag("login_hint")
            )
        }
    }
}
