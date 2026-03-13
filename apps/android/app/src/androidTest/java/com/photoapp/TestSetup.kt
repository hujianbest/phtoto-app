package com.photoapp

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithTag

fun loginIfNeeded(rule: ComposeTestRule) {
    try {
        rule.onNodeWithTag("login_button").performClick()
        rule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                rule.onNodeWithText("发现页", useUnmergedTree = true).assertIsDisplayed()
                true
            }.getOrDefault(false)
        }
    } catch (_: AssertionError) {
        // Already logged in route.
    }
}
