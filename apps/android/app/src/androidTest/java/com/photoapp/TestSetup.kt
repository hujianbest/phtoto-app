package com.photoapp

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithTag

fun loginIfNeeded(rule: ComposeTestRule) {
    try {
        rule.onNodeWithTag("login_button").performClick()
    } catch (_: AssertionError) {
        // Already logged in route.
    }
}
