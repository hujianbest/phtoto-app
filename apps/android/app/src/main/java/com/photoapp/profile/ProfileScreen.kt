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
    email: String,
    challengeJoinedAt: String?,
    reportHistory: List<String>,
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
            text = "账号: ${email.ifBlank { "未记录" }}",
            modifier = Modifier.testTag("profile_email")
        )
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
        Text(
            text = if (challengeJoinedAt.isNullOrBlank()) "挑战状态: 未参加" else "挑战状态: 已参加",
            modifier = Modifier.testTag("profile_challenge_status")
        )
        Text(
            text = "举报记录: ${reportHistory.size}",
            modifier = Modifier.testTag("profile_report_count")
        )
        if (reportHistory.isNotEmpty()) {
            Text(
                text = "最近举报: ${reportHistory.first()}",
                modifier = Modifier.testTag("profile_report_latest")
            )
        }
        Button(
            onClick = onBack,
            modifier = Modifier.testTag("profile_back_button")
        ) {
            Text(text = "返回发现页")
        }
    }
}
