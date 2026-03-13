package com.photoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ReviewHelpfulTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun clickHelpful_countPlusOne() {
        loginIfNeeded(composeRule)
        composeRule.onNodeWithTag("open_review_sheet_button").performClick()

        composeRule.onNodeWithTag("review_helpful_count").assertTextEquals("0")
        composeRule.onNodeWithTag("review_helpful_button").assertIsDisplayed()
        composeRule.onNodeWithTag("review_helpful_button").performClick()
        composeRule.onNodeWithTag("review_helpful_count").assertTextEquals("1")
    }
}
