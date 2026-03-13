package com.photoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import org.junit.Rule
import org.junit.Test

class AuthFlowTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun loginButton_navigatesToDiscoverPage() {
        resetAppState()
        ActivityScenario.launch(MainActivity::class.java)

        composeRule.onNodeWithText("登录页").assertIsDisplayed()
        composeRule.onNodeWithTag("login_button").performClick()
        composeRule.onNodeWithText("发现页").assertIsDisplayed()
    }
}
