package com.photoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import com.photoapp.post.PostRepository
import org.junit.Rule
import org.junit.Test

class PostPublishTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun createPost_thenOpenFeed_cardIsVisible() {
        resetAppState()
        PostRepository.clearForTest()
        ActivityScenario.launch(MainActivity::class.java)

        composeRule.onNodeWithTag("login_button").performClick()
        composeRule.onNodeWithTag("open_create_post_button").performClick()

        composeRule.onNodeWithTag("create_post_title_input").performTextInput("晨雾山谷")
        composeRule.onNodeWithTag("create_post_intent_input").performTextInput("练习逆光层次")
        composeRule.onNodeWithTag("create_post_image_input").performTextInput("https://cdn.example.com/morning.jpg")
        composeRule.onNodeWithTag("create_post_exif_input").performTextInput("f/2.8 ISO200 1/125")
        composeRule.onNodeWithTag("create_post_author_input").performTextInput("Leo")
        composeRule.onNodeWithTag("publish_post_button").performClick()

        composeRule.onNodeWithText("发现页").assertIsDisplayed()
        composeRule.onNodeWithText("晨雾山谷").assertIsDisplayed()
        composeRule.onNodeWithText("练习逆光层次").assertIsDisplayed()
        composeRule.onNodeWithText("图: https://cdn.example.com/morning.jpg").assertIsDisplayed()
        composeRule.onNodeWithText("参数: f/2.8 ISO200 1/125").assertIsDisplayed()
        composeRule.onNodeWithText("作者: Leo").assertIsDisplayed()
    }
}
