package com.example.myapplication.feature.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.core.ui.GlimTopBar
import com.example.myapplication.core.ui.TitleAlignment
import com.example.myapplication.feature.login.component.GlimButton
import com.example.myapplication.feature.signup.component.*

enum class SignUpStep(val progress: Float) {
    Email(0.25f),
    Code(0.5f),
    Password(0.75f),
    Profile(1f)
}

@Composable
internal fun SignUpRoute() {
    SignUpScreen()
}

@Composable
private fun SignUpScreen() {
    var currentStep by remember { mutableStateOf(SignUpStep.Email) }

    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = currentStep != SignUpStep.Email) {
        currentStep = when (currentStep) {
            SignUpStep.Code -> SignUpStep.Email
            SignUpStep.Password -> SignUpStep.Code
            SignUpStep.Profile -> SignUpStep.Password
            else -> currentStep
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        GlimTopBar(
            title = stringResource(id = R.string.login_signup),
            showBack = false,
            alignment = TitleAlignment.Center,
            titleColor = Color.Black,
            titleSize = 20.sp
        )

        ProgressIndicatorBar(progress = currentStep.progress)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (currentStep) {
                SignUpStep.Email -> EmailAuthInputContent(
                    value = email,
                    onValueChange = { email = it },
                    error = null // add validation later
                )

                SignUpStep.Code -> EmailVerificationCodeInputContent(
                    value = code,
                    onValueChange = { code = it },
                    error = null
                )

                SignUpStep.Password -> PasswordConfirmInputContent(
                    password = password,
                    onPasswordChange = { password = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    passwordError = null,
                    confirmPasswordError = null
                )

                SignUpStep.Profile -> UserProfileInputContent(
                    name = name,
                    onNameChange = { name = it },
                    birthYear = birthYear,
                    onBirthYearChange = { birthYear = it },
                    selectedGender = gender,
                    onGenderSelect = { gender = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            GlimButton(
                text = stringResource(id = R.string.app_name),
                onClick = {
                    currentStep = when (currentStep) {
                        SignUpStep.Email -> SignUpStep.Code
                        SignUpStep.Code -> SignUpStep.Password
                        SignUpStep.Password -> SignUpStep.Profile
                        SignUpStep.Profile -> SignUpStep.Profile
                    }
                }
            )
        }
    }
}

@Composable
fun ProgressIndicatorBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSignUpScreen() {
    SignUpScreen()
}
