package com.photoapp

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

private val Context.authDataStore by preferencesDataStore(name = "auth")

class ReviewHelpfulTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun clickHelpful_countPlusOne() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.authDataStore.edit { it.clear() }
        }

        composeRule.onNodeWithTag("login_button").performClick()
        composeRule.onNodeWithTag("open_review_sheet_button").performClick()

        composeRule.onNodeWithTag("review_helpful_count").assertTextEquals("0")
        composeRule.onNodeWithTag("review_helpful_button").assertIsDisplayed()
        composeRule.onNodeWithTag("review_helpful_button").performClick()
        composeRule.onNodeWithTag("review_helpful_count").assertTextEquals("1")
    }
}
