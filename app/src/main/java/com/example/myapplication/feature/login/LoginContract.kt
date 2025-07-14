package com.example.myapplication.feature.login

/** UI 상태를 한눈에 보여주는 data class */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
) {
    /** 이메일·비밀번호 모두 유효해야 로그인 버튼 활성화 */
    val isLoginEnabled: Boolean
        get() = emailError == null
                && passwordError == null
                && email.isNotBlank()
                && password.isNotBlank()
}

/** 스낵바/토스트 같은 일회성 효과 */
sealed interface LoginSideEffect {
    object NavigateMain : LoginSideEffect
    data class ShowError(val message: String) : LoginSideEffect
}
