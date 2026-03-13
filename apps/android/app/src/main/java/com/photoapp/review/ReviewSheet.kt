package com.photoapp.review

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ReviewSheet(onBack: () -> Unit) {
    var composition by rememberSaveable { mutableStateOf("") }
    var light by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var story by rememberSaveable { mutableStateOf("") }
    var postProcess by rememberSaveable { mutableStateOf("") }
    var helpfulCount by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "结构化点评")
        OutlinedTextField(
            value = composition,
            onValueChange = { composition = it },
            label = { Text("构图") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("review_composition_input")
        )
        OutlinedTextField(
            value = light,
            onValueChange = { light = it },
            label = { Text("光线") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("review_light_input")
        )
        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("色彩") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("review_color_input")
        )
        OutlinedTextField(
            value = story,
            onValueChange = { story = it },
            label = { Text("叙事") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("review_story_input")
        )
        OutlinedTextField(
            value = postProcess,
            onValueChange = { postProcess = it },
            label = { Text("后期建议") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("review_postprocess_input")
        )
        Button(
            onClick = { helpfulCount += 1 },
            modifier = Modifier.testTag("review_helpful_button")
        ) {
            Text(text = "有帮助")
        }
        Text(
            text = helpfulCount.toString(),
            modifier = Modifier.testTag("review_helpful_count")
        )
        Button(
            onClick = onBack,
            modifier = Modifier.testTag("review_back_button")
        ) {
            Text(text = "返回发现页")
        }
    }
}
