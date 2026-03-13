package com.photoapp

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

private val Context.authDataStore by preferencesDataStore(name = "auth")

class AuthFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginButton_navigatesToDiscoverPage() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.authDataStore.edit { it.clear() }
        }

        composeRule.onNodeWithText("登录页").assertIsDisplayed()
        composeRule.onNodeWithTag("login_button").performClick()
        composeRule.onNodeWithText("发现页").assertIsDisplayed()
    }
}
