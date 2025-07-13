package com.example.myapplication.feature.login

/**
 * 화면에 필요한 모든 상태를 단일 data class 로 정의합니다.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
) {
    val isFormValid: Boolean
        get() = emailError == null
                && passwordError == null
                && email.isNotBlank()
                && password.isNotBlank()
}

sealed interface LoginSideEffect {
    data object NavigateMain : LoginSideEffect
    data class ShowError(val message: String) : LoginSideEffect
}
