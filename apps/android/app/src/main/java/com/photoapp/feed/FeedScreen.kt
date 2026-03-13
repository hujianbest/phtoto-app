package com.photoapp.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.photoapp.post.Post
import coil.compose.AsyncImage

@Composable
fun FeedScreen(
    posts: List<Post>,
    onOpenCreatePost: () -> Unit,
    onOpenReviewSheet: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenChallenge: () -> Unit,
    onReportPost: (postId: String) -> Unit,
    onSelectRecommended: () -> Unit,
    onSelectFollowing: () -> Unit,
    onFollowAuthor: (authorName: String) -> Unit,
    reportHint: String?
) {
    var filterKeyword by remember { mutableStateOf("") }
    val visiblePosts = remember(posts, filterKeyword) {
        val keyword = filterKeyword.trim()
        if (keyword.isEmpty()) {
            posts
        } else {
            posts.filter { post ->
                post.title.contains(keyword, ignoreCase = true) ||
                    post.intent.contains(keyword, ignoreCase = true) ||
                    post.authorName.contains(keyword, ignoreCase = true) ||
                    post.exifSummary.contains(keyword, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "发现页",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = onSelectRecommended,
                    label = { Text("推荐流") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("feed_tab_recommended_button")
                )
                AssistChip(
                    onClick = onSelectFollowing,
                    label = { Text("关注流") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("feed_tab_following_button")
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onOpenCreatePost, modifier = Modifier.testTag("open_create_post_button")) {
                    Text(text = "去发布")
                }
                Button(onClick = onOpenReviewSheet, modifier = Modifier.testTag("open_review_sheet_button")) {
                    Text(text = "去点评")
                }
                Button(onClick = onOpenProfile, modifier = Modifier.testTag("open_profile_button")) {
                    Text(text = "个人主页")
                }
            }
            Button(
                onClick = onOpenChallenge,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_challenge_button")
            ) {
                Text(text = "本周挑战：夜景长曝光")
            }
            OutlinedTextField(
                value = filterKeyword,
                onValueChange = { filterKeyword = it },
                label = { Text("按标题/作者/参数筛选") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("feed_filter_input")
            )
            if (!reportHint.isNullOrBlank()) {
                Text(
                    text = reportHint,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("feed_report_hint")
                )
            }
            if (visiblePosts.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(
                        text = "暂无内容",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visiblePosts, key = { it.id }) { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                ) {
                                    AsyncImage(
                                        model = post.imageUrl,
                                        contentDescription = "post-cover",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color(0xFF2E3E5C),
                                                        Color(0xFF1E2230),
                                                        Color(0xFF11151E)
                                                    )
                                                )
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color(0x77000000),
                                                        Color(0xCC000000)
                                                    )
                                                )
                                            )
                                    )
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = post.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White
                                        )
                                        Text(
                                            text = post.authorName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFE0E3EA)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = post.intent,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "图: ${post.imageUrl}", modifier = Modifier.testTag("feed_post_image"))
                                Text(text = "参数: ${post.exifSummary}", modifier = Modifier.testTag("feed_post_exif"))
                                Text(text = "作者: ${post.authorName}", modifier = Modifier.testTag("feed_post_author"))
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onReportPost(post.id) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("feed_report_button")
                                    ) {
                                        Text(text = "举报")
                                    }
                                    Button(
                                        onClick = { onFollowAuthor(post.authorName) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("feed_follow_author_button")
                                    ) {
                                        Text(text = "关注作者")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
