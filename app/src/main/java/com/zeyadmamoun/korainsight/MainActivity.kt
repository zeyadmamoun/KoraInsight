package com.zeyadmamoun.korainsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zeyadmamoun.korainsight.ui.navigation.KoraInsightApp
import com.zeyadmamoun.korainsight.ui.theme.KoraInsightTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoraInsightTheme {
                KoraInsightApp()
            }
        }
    }
}