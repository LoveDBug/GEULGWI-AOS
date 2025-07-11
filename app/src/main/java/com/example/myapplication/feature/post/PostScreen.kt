package com.example.myapplication.feature.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun PostRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {

    Text(
        text = "post"
    )
}