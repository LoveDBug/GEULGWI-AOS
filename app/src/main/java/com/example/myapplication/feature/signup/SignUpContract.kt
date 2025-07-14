package com.example.myapplication.feature.signup

/** UiState 정의 */
data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.Email,
    val email: String = "",
    val code: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val birthYear: String = "",
    val gender: String? = null,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val codeError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

sealed interface SignUpSideEffect {
    data object NavigateToMain : SignUpSideEffect
    data class ShowToast(val message: String) : SignUpSideEffect
}

enum class SignUpStep(val progress: Float) {
    Email(0.25f),
    Code(0.5f),
    Password(0.75f),
    Profile(1f)
}
