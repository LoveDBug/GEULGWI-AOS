package com.example.myapplication.feature.login.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.login.LoginRoute

fun NavController.navigateToLogin() {
    navigate(Route.Login)
}

fun NavGraphBuilder.loginNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.Login> {
        LoginRoute(
            padding = padding
        )
    }
}