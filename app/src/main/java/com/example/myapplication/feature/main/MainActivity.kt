package com.example.myapplication.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.core.navigation.LaunchedNavigator
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigator: MainNavController = rememberMainNavController()
            LaunchedNavigator(navigator.navController)
            MyApplicationTheme {
                MainScreen(
                    navigator = navigator,
                )
            }
        }
    }
}
