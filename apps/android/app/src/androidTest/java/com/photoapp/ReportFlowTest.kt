package com.photoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.photoapp.post.PostRepository
import org.junit.Rule
import org.junit.Test

class ReportFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun reportPost_thenProfileShowsReportSection() {
        PostRepository.clearForTest()
        loginIfNeeded(composeRule)

        composeRule.onNodeWithTag("open_create_post_button").performClick()
        composeRule.onNodeWithTag("create_post_title_input").performTextInput("夜色码头")
        composeRule.onNodeWithTag("create_post_intent_input").performTextInput("测试举报流程")
        composeRule.onNodeWithTag("create_post_image_input").performTextInput("https://cdn.example.com/night.jpg")
        composeRule.onNodeWithTag("create_post_exif_input").performTextInput("f/8 ISO100 2s")
        composeRule.onNodeWithTag("create_post_author_input").performTextInput("Mia")
        composeRule.onNodeWithTag("publish_post_button").performClick()

        composeRule.onNodeWithTag("feed_report_button").performClick()
        composeRule.onNodeWithTag("report_reason_input").performTextInput("疑似无关广告")
        composeRule.onNodeWithTag("report_submit_button").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithTag("feed_report_hint").assertIsDisplayed()
                true
            }.getOrDefault(false)
        }

        composeRule.onNodeWithTag("open_profile_button").performClick()
        composeRule.onNodeWithTag("profile_report_count").assertIsDisplayed()
        composeRule.onNodeWithText("个人主页").assertIsDisplayed()
    }
}
