package com.photoapp.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.photoapp.post.Post

@Composable
fun ProfileScreen(
    posts: List<Post>,
    onBack: () -> Unit
) {
    val worksCount = posts.size
    val growthScore = worksCount * 10
    val growthLevel = when {
        worksCount >= 10 -> "稳定产出"
        worksCount >= 5 -> "持续进步"
        worksCount >= 1 -> "起步创作"
        else -> "等待首作"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "个人主页")
        Text(
            text = "作品数量: $worksCount",
            modifier = Modifier.testTag("profile_works_count")
        )
        Text(
            text = "成长积分: $growthScore",
            modifier = Modifier.testTag("profile_growth_score")
        )
        Text(
            text = "成长阶段: $growthLevel",
            modifier = Modifier.testTag("profile_growth_level")
        )
        Button(
            onClick = onBack,
            modifier = Modifier.testTag("profile_back_button")
        ) {
            Text(text = "返回发现页")
        }
    }
}
