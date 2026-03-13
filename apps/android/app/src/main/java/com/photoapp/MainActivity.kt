package com.photoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.photoapp.navigation.AppNavGraph
import com.photoapp.ui.theme.PhotoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoAppTheme(darkTheme = true) {
                AppNavGraph()
            }
        }
    }
}
