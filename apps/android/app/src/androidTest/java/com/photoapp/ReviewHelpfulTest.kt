package com.photoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import org.junit.Rule
import org.junit.Test

class ReviewHelpfulTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun clickHelpful_countPlusOne() {
        resetAppState()
        ActivityScenario.launch(MainActivity::class.java)

        composeRule.onNodeWithTag("login_button").performClick()
        composeRule.onNodeWithTag("open_review_sheet_button").performClick()

        composeRule.onNodeWithTag("review_helpful_count").assertTextEquals("0")
        composeRule.onNodeWithTag("review_helpful_button").assertIsDisplayed()
        composeRule.onNodeWithTag("review_helpful_button").performClick()
        composeRule.onNodeWithTag("review_helpful_count").assertTextEquals("1")
    }
}
