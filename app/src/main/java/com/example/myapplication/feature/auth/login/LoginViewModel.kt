package com.example.myapplication.feature.auth.login

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.domain.usecase.auth.LoginUseCase
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.core.navigation.Navigator
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.auth.login.component.SocialProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val navigator: Navigator,
    private val loginUseCase: LoginUseCase
) : ViewModel(), ContainerHost<LoginUiState, LoginSideEffect> {

    companion object {
        private val PASSWORD_REGEX = Regex(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\\$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?]).{8,16}$"
        )
    }

    override val container = container<LoginUiState, LoginSideEffect>(initialState = LoginUiState())

    fun onEmailChanged(email: String) = intent {
        val error = validateEmail(email)
        reduce { state.copy(email = email, emailError = error) }
    }

    fun onPasswordChanged(password: String) = intent {
        val error = validatePassword(password)
        reduce { state.copy(password = password, passwordError = error) }
    }

    fun onLoginClicked() = intent {
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        if (emailError != null || passwordError != null) {
            reduce { state.copy(emailError = emailError, passwordError = passwordError) }
            postSideEffect(LoginSideEffect.ShowError(emailError ?: passwordError!!))
            return@intent
        }
        reduce { state.copy(isLoading = true) }
        delay(1_000)
    }

    fun navigateToSignUp() = intent { navigator.navigate(Route.SignUp) }

    fun navigateToHome() = intent { navigator.navigate(BottomTabRoute.Home) }

    fun navigateToForgotPassword() = intent {}

    fun navigateToSocialLogin(socialProvider: SocialProvider) = intent {}

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "이메일을 입력해주세요."
        !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() -> "유효한 이메일을 입력해주세요."
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "비밀번호를 입력해주세요."
        !PASSWORD_REGEX.matches(password) -> "대소문자·숫자·특수문자 포함 8~16자"
        else -> null
    }
}
