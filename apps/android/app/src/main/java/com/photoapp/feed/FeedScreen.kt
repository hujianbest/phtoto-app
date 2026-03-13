package com.photoapp.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.photoapp.post.Post

@Composable
fun FeedScreen(
    posts: List<Post>,
    onOpenCreatePost: () -> Unit,
    onOpenReviewSheet: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "发现页")
        Button(
            onClick = onOpenCreatePost,
            modifier = Modifier.testTag("open_create_post_button")
        ) {
            Text(text = "去发布")
        }
        Button(
            onClick = onOpenReviewSheet,
            modifier = Modifier.testTag("open_review_sheet_button")
        ) {
            Text(text = "去点评")
        }
        Button(
            onClick = onOpenProfile,
            modifier = Modifier.testTag("open_profile_button")
        ) {
            Text(text = "个人主页")
        }
        if (posts.isEmpty()) {
            Text(text = "暂无内容")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = post.title)
                            Text(text = post.intent)
                            Text(text = "图: ${post.imageUrl}", modifier = Modifier.testTag("feed_post_image"))
                            Text(text = "参数: ${post.exifSummary}", modifier = Modifier.testTag("feed_post_exif"))
                            Text(text = "作者: ${post.authorName}", modifier = Modifier.testTag("feed_post_author"))
                        }
                    }
                }
            }
        }
    }
}
