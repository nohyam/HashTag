package com.nohyam.hashtag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nohyam.hashtag.navigation.HashTagNavHost
import com.nohyam.hashtag.ui.theme.HashTagTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HashTagTheme {
                HashTagNavHost()
            }
        }
    }
}
