package com.photoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ChallengeFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun joinChallenge_thenStatusVisible() {
        loginIfNeeded(composeRule)
        composeRule.onNodeWithTag("open_challenge_button").performClick()
        composeRule.onNodeWithTag("challenge_join_status").assertIsDisplayed()

        runCatching {
            composeRule.onNodeWithTag("challenge_join_button").performClick()
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithText("参与状态：已参与", useUnmergedTree = true).assertIsDisplayed()
                true
            }.getOrDefault(false)
        }
    }
}
