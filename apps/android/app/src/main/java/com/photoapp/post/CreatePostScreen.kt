package com.photoapp.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
fun CreatePostScreen(
    onPublish: (
        title: String,
        intent: String,
        imageUrl: String,
        exifSummary: String,
        authorName: String
    ) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var intent by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var exifSummary by rememberSaveable { mutableStateOf("") }
    var authorName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "发布作品")
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("标题") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_post_title_input")
        )
        OutlinedTextField(
            value = intent,
            onValueChange = { intent = it },
            label = { Text("创作意图") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_post_intent_input")
        )
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("图片地址") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_post_image_input")
        )
        OutlinedTextField(
            value = exifSummary,
            onValueChange = { exifSummary = it },
            label = { Text("参数摘要") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_post_exif_input")
        )
        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("作者") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_post_author_input")
        )
        Button(
            onClick = { onPublish(title, intent, imageUrl, exifSummary, authorName) },
            modifier = Modifier.testTag("publish_post_button")
        ) {
            Text(text = "发布")
        }
    }
}
