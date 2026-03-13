package com.photoapp.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ReportScreen(
    targetPostId: String,
    onSubmit: (reason: String) -> Unit,
    onBack: () -> Unit
) {
    var reason by rememberSaveable { mutableStateOf("") }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "举报作品", style = MaterialTheme.typography.headlineSmall)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "目标ID: $targetPostId", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("举报原因") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_reason_input")
                    )
                    Button(
                        onClick = { onSubmit(reason) },
                        enabled = reason.trim().isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_submit_button")
                    ) {
                        Text(text = "提交举报")
                    }
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_back_button")
                    ) {
                        Text(text = "返回发现页")
                    }
                }
            }
        }
    }
}
