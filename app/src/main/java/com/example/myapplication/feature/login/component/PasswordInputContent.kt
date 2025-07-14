package com.example.myapplication.feature.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun PasswordInputContent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null
) {
    val labelText = error ?: stringResource(id = R.string.login_password_placeholder)

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        isError = error != null,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        label = {
            Text(
                text = labelText,
                color = if (error != null)
                    MaterialTheme.colorScheme.error
                else
                    Color.Gray,
                fontSize = 14.sp
            )
        }
    )
}
