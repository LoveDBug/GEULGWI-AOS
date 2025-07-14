package com.example.myapplication.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.core.ui.GlimTopBar
import com.example.myapplication.core.ui.TitleAlignment
import com.example.myapplication.feature.login.component.EmailInputTextField
import com.example.myapplication.feature.login.component.GlimButton
import com.example.myapplication.feature.login.component.PasswordInputTextField
import com.example.myapplication.feature.login.component.SocialButton
import com.example.myapplication.feature.login.component.SocialProvider
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun LoginRoute(
    onNavigateMain: () -> Unit,
    onNavigateSignUp: () -> Unit,
    onNavigateForgot: () -> Unit,
    onSocialLogin: (SocialProvider) -> Unit,
    onGuest: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    viewModel.collectSideEffect { effect ->
        when (effect) {
            LoginSideEffect.NavigateMain -> onNavigateMain()
            is LoginSideEffect.ShowError -> Unit
        }
    }

    LoginScreen(
        state = uiState,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClicked = viewModel::onLoginClicked,
        onSignUpClicked = onNavigateSignUp,
        onForgotPassword = onNavigateForgot,
        onSocialLogin = onSocialLogin,
        onGuest = onGuest
    )
}

@Composable
internal fun LoginScreen(
    state: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onForgotPassword: () -> Unit,
    onSocialLogin: (SocialProvider) -> Unit,
    onGuest: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GlimTopBar(
            title = stringResource(id = R.string.login_title),
            showBack = false,
            alignment = TitleAlignment.Center,
            titleColor = Color.Black,
            titleSize = 20.sp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(id = R.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            Spacer(Modifier.height(48.dp))

            EmailInputTextField(
                value = state.email,
                onValueChange = onEmailChanged,
                error = state.emailError
            )

            Spacer(Modifier.height(16.dp))

            PasswordInputTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                error = state.passwordError
            )

            Spacer(Modifier.height(24.dp))

            GlimButton(
                text = if (state.isLoading) stringResource(R.string.login_loading) else stringResource(R.string.login_button),
                onClick = onLoginClicked,
                enabled = state.isLoginEnabled && !state.isLoading
            )

            Spacer(Modifier.height(12.dp))
            Row {
                TextButton(onClick = onSignUpClicked) { Text(stringResource(id = R.string.login_signup)) }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onForgotPassword) { Text(stringResource(id = R.string.login_forgot_password)) }
            }

            Spacer(Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(stringResource(R.string.login_sns_title), style = MaterialTheme.typography.bodySmall)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialButton(SocialProvider.GOOGLE) { onSocialLogin(SocialProvider.GOOGLE) }
                SocialButton(SocialProvider.KAKAO) { onSocialLogin(SocialProvider.KAKAO) }
                SocialButton(SocialProvider.NAVER) { onSocialLogin(SocialProvider.NAVER) }
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onGuest) {
                Text(stringResource(R.string.login_guest))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(name = "Empty Form", showBackground = true)
@Composable
fun PreviewLoginScreen_Empty() {
    LoginScreen(
        state = LoginUiState(
            email = "",
            password = "",
            isLoading = false,
            emailError = null,
            passwordError = null
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {}
    )
}

@Preview(name = "With Errors", showBackground = true)
@Composable
fun PreviewLoginScreen_Errors() {
    LoginScreen(
        state = LoginUiState(
            email = "invalid-email",
            password = "short",
            isLoading = false,
            emailError = "유효한 이메일 형식을 입력해주세요.",
            passwordError = "8~16자, 영문 대/소문자·숫자·특수문자 포함"
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {}
    )
}

@Preview(name = "Valid Input", showBackground = true)
@Composable
fun PreviewLoginScreen_Valid() {
    LoginScreen(
        state = LoginUiState(
            email = "user@example.com",
            password = "Aa1!abcd",
            isLoading = false,
            emailError = null,
            passwordError = null
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {}
    )
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun PreviewLoginScreen_Loading() {
    LoginScreen(
        state = LoginUiState(
            email = "user@example.com",
            password = "Aa1!abcd",
            isLoading = true,
            emailError = null,
            passwordError = null
        ),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {}
    )
}