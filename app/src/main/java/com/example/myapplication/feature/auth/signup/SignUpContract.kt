package com.example.myapplication.feature.auth.signup

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
) {
    val isStepValid: Boolean
        get() = when (currentStep) {
            SignUpStep.Email -> email.isNotBlank()
            SignUpStep.Code -> code.isNotBlank()
            SignUpStep.Password -> password.isNotBlank() && confirmPassword.isNotBlank()
            SignUpStep.Profile -> name.isNotBlank() && birthYear.isNotBlank() && gender != null
        }
}

sealed interface SignUpSideEffect {
    object NavigateToMain : SignUpSideEffect
    data class ShowToast(val message: String) : SignUpSideEffect
}

enum class SignUpStep(val progress: Float) {
    Email(0.25f),
    Code(0.5f),
    Password(0.75f),
    Profile(1f);

    fun next(): SignUpStep? = when (this) {
        Email -> Code
        Code -> Password
        Password -> Profile
        Profile -> null
    }

    fun prev(): SignUpStep? = when (this) {
        Profile -> Password
        Password -> Code
        Code -> Email
        Email -> null
    }
}