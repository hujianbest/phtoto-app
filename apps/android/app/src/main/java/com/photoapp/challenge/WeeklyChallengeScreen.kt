package com.photoapp.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun WeeklyChallengeScreen(
    joinedAt: String?,
    onJoinChallenge: () -> Unit,
    onGoCreatePost: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "每周挑战", style = MaterialTheme.typography.headlineSmall)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "主题：夜景长曝光")
                    Text(text = "建议：使用三脚架，快门 1s 以上，记录城市流光。")
                    Text(
                        text = if (joinedAt.isNullOrBlank()) "参与状态：未参与" else "参与状态：已参与",
                        modifier = Modifier.testTag("challenge_join_status")
                    )
                    Button(
                        onClick = onJoinChallenge,
                        enabled = joinedAt.isNullOrBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("challenge_join_button")
                    ) {
                        Text(text = if (joinedAt.isNullOrBlank()) "参加本周挑战" else "已参加")
                    }
                    Button(
                        onClick = onGoCreatePost,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("challenge_go_publish_button")
                    ) {
                        Text(text = "去发布挑战作品")
                    }
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("challenge_back_button")
                    ) {
                        Text(text = "返回发现页")
                    }
                }
            }
        }
    }
}
