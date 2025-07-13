package com.example.myapplication.feature.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ContentAlpha
import com.example.myapplication.R
import com.example.myapplication.core.ui.GlimTopBar
import com.example.myapplication.core.ui.TitleAlignment
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
            LoginSideEffect.NavigateMain ->
                onNavigateMain()

            is LoginSideEffect.ShowError ->
                /* TODO: 스낵바로 effect.message 표시 */
                Unit
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
            title = "로그인",
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
                "로그인하고\n풍부하게 감성을 채워보세요.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            Spacer(Modifier.height(48.dp))

            // 이메일 필드: 에러일 때 label 에러 메시지, 색상도 errorColor 로
            TextField(
                value = state.email,
                onValueChange = onEmailChanged,
                singleLine = true,
                isError = state.emailError != null,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = state.emailError ?: "이메일을 입력해주세요.",
                        color = if (state.emailError != null)
                            MaterialTheme.colorScheme.error
                        else
                            LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                    )
                }
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = state.passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = state.passwordError ?: "비밀번호를 입력해주세요.",
                        color = if (state.passwordError != null)
                            MaterialTheme.colorScheme.error
                        else
                            LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                    )
                }
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onLoginClicked,
                enabled = state.isFormValid && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (state.isLoading) {
                    Text("로딩중")
                } else {
                    Text("로그인")
                }

            }

            Spacer(Modifier.height(12.dp))
            Row {
                TextButton(onClick = onSignUpClicked) { Text("회원가입") }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onForgotPassword) { Text("비밀번호를 잊어버렸나요?") }
            }

            Spacer(Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(" SNS 간편 로그인 ", style = MaterialTheme.typography.bodySmall)
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
                Text("비회원으로 둘러보기")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SocialButton(
    provider: SocialProvider,
    onClick: () -> Unit
) {

    val (iconRes, bgColor) = when (provider) {
        SocialProvider.GOOGLE -> R.drawable.ic_google to Color.White
        SocialProvider.KAKAO -> R.drawable.ic_kakao to Color(0xFFFEE500)
        SocialProvider.NAVER -> R.drawable.ic_naver to Color(0xFF03C75A)
    }

    Surface(
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.LightGray),
        color = bgColor,
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = provider.name,
            modifier = Modifier.padding(14.dp)
        )
    }
}


enum class SocialProvider { GOOGLE, KAKAO, NAVER }

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
