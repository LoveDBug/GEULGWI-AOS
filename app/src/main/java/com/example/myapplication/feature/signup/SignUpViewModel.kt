package com.example.myapplication.feature.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/** ViewModel */
@HiltViewModel
class SignUpViewModel @Inject constructor() :
    ViewModel(), ContainerHost<SignUpUiState, SignUpSideEffect> {

    override val container = container<SignUpUiState, SignUpSideEffect>(
        initialState = SignUpUiState()
    )

    fun onEmailChanged(email: String) = intent {
        reduce { state.copy(email = email, emailError = null) }
    }

    fun onCodeChanged(code: String) = intent {
        reduce { state.copy(code = code, codeError = null) }
    }

    fun onPasswordChanged(password: String) = intent {
        reduce { state.copy(password = password, passwordError = null) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) = intent {
        reduce { state.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    fun onNameChanged(name: String) = intent {
        reduce { state.copy(name = name) }
    }

    fun onBirthYearChanged(birthYear: String) = intent {
        reduce { state.copy(birthYear = birthYear) }
    }

    fun onGenderSelected(gender: String) = intent {
        reduce { state.copy(gender = gender) }
    }

    fun onNextStep() = intent {
        val next = when (state.currentStep) {
            SignUpStep.Email -> SignUpStep.Code
            SignUpStep.Code -> SignUpStep.Password
            SignUpStep.Password -> SignUpStep.Profile
            SignUpStep.Profile -> null
        }
        next?.let {
            reduce { state.copy(currentStep = it) }
        } ?: postSideEffect(SignUpSideEffect.NavigateToMain)
    }

    fun onBackStep() = intent {
        val prev = when (state.currentStep) {
            SignUpStep.Code -> SignUpStep.Email
            SignUpStep.Password -> SignUpStep.Code
            SignUpStep.Profile -> SignUpStep.Password
            else -> null
        }
        prev?.let {
            reduce { state.copy(currentStep = it) }
        }
    }

    fun validatePasswordMatch() = intent {
        if (state.password != state.confirmPassword) {
            reduce {
                state.copy(confirmPasswordError = "비밀번호가 일치하지 않습니다.")
            }
        }
    }

    fun onSubmit() = intent {
        reduce { state.copy(isLoading = true) }
        delay(1000)
        reduce { state.copy(isLoading = false) }
        postSideEffect(SignUpSideEffect.NavigateToMain)
    }
}