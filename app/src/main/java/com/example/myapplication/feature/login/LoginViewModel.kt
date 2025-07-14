package com.example.myapplication.feature.login

import android.util.Patterns
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
        private val PASSWORD_REGEX = Regex(
            """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]).{8,16}$"""
        )
    }

    override val container = container<LoginUiState, LoginSideEffect>(
        initialState = LoginUiState()
    )

    private fun validateEmail(email: String): String? =
        when {
            email.isBlank() -> "이메일을 입력해주세요."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "유효한 이메일 형식을 입력해주세요."

            else -> null
        }

    private fun validatePassword(pw: String): String? =
        when {
            pw.isBlank() -> "비밀번호를 입력해주세요."
            !PASSWORD_REGEX.matches(pw) ->
                "대소문자, 영문, 숫자, 특수문자를 포함해 8~16자를 입력해주세요."

            else -> null
        }

    fun onEmailChanged(email: String) = intent {
        val error = validateEmail(email)
        reduce {
            state.copy(email = email, emailError = error)
        }
    }

    fun onPasswordChanged(password: String) = intent {
        val error = validatePassword(password)
        reduce {
            state.copy(password = password, passwordError = error)
        }
    }

    fun onLoginClicked() = intent {
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        if (emailError != null || passwordError != null) {
            reduce {
                state.copy(emailError = emailError, passwordError = passwordError)
            }
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        delay(1_000)

        postSideEffect(LoginSideEffect.NavigateMain)
    }
}
