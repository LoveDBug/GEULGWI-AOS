package com.example.myapplication.feature.login

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor() :
    ViewModel(),
    ContainerHost<LoginUiState, LoginSideEffect> {

    companion object {
        private val PASSWORD_REGEX =
            Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$")
    }

    override val container = container<LoginUiState, LoginSideEffect>(
        initialState = LoginUiState()
    )

    fun onEmailChanged(email: String) = intent {
        val error = when {
            email.isBlank() -> "이메일을 입력해주세요."
            !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() ->
                "유효한 이메일을 입력해주세요."
            else -> null
        }
        reduce { state.copy(email = email, emailError = error) }
    }

    fun onPasswordChanged(password: String) = intent {
        val error = when {
            password.isBlank() -> "비밀번호를 입력해주세요."
            !PASSWORD_REGEX.matches(password) ->
                "대소문자·숫자·특수문자 포함 8~16자"

            else -> null
        }
        reduce { state.copy(password = password, passwordError = error) }
    }

    fun onLoginClicked() = intent {
        val emailErr = validateEmail(state.email)
        val pwErr = validatePassword(state.password)
        if (emailErr != null || pwErr != null) {
            reduce { state.copy(emailError = emailErr, passwordError = pwErr) }
            postSideEffect(LoginSideEffect.ShowError(emailErr ?: pwErr!!))
            return@intent
        }

        reduce { state.copy(isLoading = true) }
        delay(1_000)
        postSideEffect(LoginSideEffect.NavigateMain)
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "이메일을 입력해주세요."
        !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() ->
            "유효한 이메일을 입력해주세요."

        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "비밀번호를 입력해주세요."
        !PASSWORD_REGEX.matches(password) ->
            "대소문자·숫자·특수문자 포함 8~16자"

        else -> null
    }
}
