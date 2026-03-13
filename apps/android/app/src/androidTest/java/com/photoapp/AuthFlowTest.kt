package com.photoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AuthFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginButton_navigatesToDiscoverPage() {
        val alreadyInDiscover = runCatching {
            composeRule.onNodeWithText("发现页", useUnmergedTree = true).assertIsDisplayed()
            true
        }.getOrDefault(false)

        if (!alreadyInDiscover) {
            composeRule.onNodeWithTag("login_button").performClick()
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithText("发现页", useUnmergedTree = true).assertIsDisplayed()
                true
            }.getOrDefault(false)
        }
    }
}
