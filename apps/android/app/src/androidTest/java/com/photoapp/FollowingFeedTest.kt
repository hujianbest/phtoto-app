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

class FollowingFeedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun followAuthor_thenSwitchToFollowing_feedContainsPost() {
        PostRepository.clearForTest()
        loginIfNeeded(composeRule)

        composeRule.onNodeWithTag("open_create_post_button").performClick()
        composeRule.onNodeWithTag("create_post_title_input").performTextInput("关注流样例")
        composeRule.onNodeWithTag("create_post_intent_input").performTextInput("验证关注流切换")
        composeRule.onNodeWithTag("create_post_image_input").performTextInput("https://cdn.example.com/following.jpg")
        composeRule.onNodeWithTag("create_post_exif_input").performTextInput("f/4 ISO200 1/60")
        composeRule.onNodeWithTag("create_post_author_input").performTextInput("alice@example.com")
        composeRule.onNodeWithTag("publish_post_button").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithText("关注流样例", useUnmergedTree = true).assertIsDisplayed()
                true
            }.getOrDefault(false)
        }

        composeRule.onNodeWithTag("feed_follow_author_button").performClick()
        composeRule.onNodeWithTag("feed_tab_following_button").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithText("关注流样例", useUnmergedTree = true).assertIsDisplayed()
                true
            }.getOrDefault(false)
        }
    }
}
