package com.example.myapplication.feature.auth.signup

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.domain.usecase.auth.CertifyValidCodeUseCase
import com.example.myapplication.core.domain.usecase.auth.SignUpUseCase
import com.example.myapplication.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
internal class SignUpViewModel @Inject constructor(
    private val navigator: Navigator,
    private val signUpUseCase: SignUpUseCase,
    private val certifyValidCodeUseCase: CertifyValidCodeUseCase
) : ViewModel(), ContainerHost<SignUpUiState, SignUpSideEffect> {

    companion object {
        private val PASSWORD_REGEX = Regex(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$"
        )
        private val CODE_REGEX = Regex("^[0-9]{6}$")
        private val BIRTH_YEAR_REGEX = Regex("^[0-9]{4}$")
        private val NAME_REGEX = Regex("^.{2,16}$")
    }

    override val container = container<SignUpUiState, SignUpSideEffect>(SignUpUiState())

    fun onEmailChanged(email: String) = intent {
        val error = if (email.isNotBlank()) validateEmail(email) else null
        reduce { state.copy(email = email, emailError = error) }
    }

    fun onCodeChanged(code: String) = intent {
        // 숫자만 입력되도록 필터링
        val filteredCode = code.filter { it.isDigit() }.take(6)
        val error = if (filteredCode.isNotBlank()) validateCode(filteredCode) else null
        reduce { state.copy(code = filteredCode, codeError = error) }
    }

    fun onPasswordChanged(password: String) = intent {
        val error = if (password.isNotBlank()) validatePassword(password) else null
        val confirmError =
            if (state.confirmPassword.isNotBlank() && password != state.confirmPassword) {
                "비밀번호가 일치하지 않습니다."
            } else null
        reduce {
            state.copy(
                password = password,
                passwordError = error,
                confirmPasswordError = confirmError
            )
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) = intent {
        val error = if (confirmPassword.isNotBlank() && confirmPassword != state.password) {
            "비밀번호가 일치하지 않습니다."
        } else null
        reduce { state.copy(confirmPassword = confirmPassword, confirmPasswordError = error) }
    }

    fun onNameChanged(name: String) = intent {
        val error = if (name.isNotBlank()) validateName(name) else null
        reduce { state.copy(name = name, nameError = error) }
    }

    fun onBirthYearChanged(birthYear: String) = intent {
        // 숫자만 입력되도록 필터링
        val filteredYear = birthYear.filter { it.isDigit() }.take(4)
        val error = if (filteredYear.isNotBlank()) validateBirthYear(filteredYear) else null
        reduce { state.copy(birthYear = filteredYear, birthYearError = error) }
    }

    fun onGenderSelected(gender: String) = intent {
        reduce { state.copy(gender = gender) }
    }

    fun onNextStep() = intent {
        when (state.currentStep) {
            SignUpStep.Email -> {
                val error = validateEmail(state.email)
                if (error != null) {
                    postSideEffect(SignUpSideEffect.ShowToast(error))
                    reduce { state.copy(emailError = error) }
                    return@intent
                }

            }

            SignUpStep.Code -> {
                val error = validateCode(state.code)
                if (error != null) {
                    postSideEffect(SignUpSideEffect.ShowToast(error))
                    reduce { state.copy(codeError = error) }
                    return@intent
                }
            }

            SignUpStep.Password -> {
                val passwordError = validatePassword(state.password)
                val confirmError = if (state.password != state.confirmPassword) {
                    "비밀번호가 일치하지 않습니다."
                } else null

                if (passwordError != null || confirmError != null) {
                    val errorMessage = passwordError ?: confirmError!!
                    postSideEffect(SignUpSideEffect.ShowToast(errorMessage))
                    reduce {
                        state.copy(
                            passwordError = passwordError,
                            confirmPasswordError = confirmError
                        )
                    }
                    return@intent
                }
            }

            SignUpStep.Profile -> {
                val nameError = validateName(state.name)
                val birthYearError = validateBirthYear(state.birthYear)
                val genderError = if (state.gender == null) "성별을 선택해주세요." else null

                if (nameError != null || birthYearError != null || genderError != null) {
                    val errorMessage = nameError ?: birthYearError ?: genderError!!
                    postSideEffect(SignUpSideEffect.ShowToast(errorMessage))
                    reduce {
                        state.copy(
                            nameError = nameError,
                            birthYearError = birthYearError
                        )
                    }
                    return@intent
                }
                return@intent
            }
        }

        state.currentStep.next()?.let { next ->
            reduce { state.copy(currentStep = next) }
        }
    }

    fun onBackStep() = intent {
        state.currentStep.prev()?.let { prev ->
            reduce { state.copy(currentStep = prev) }
        } ?: navigator.navigateBack()
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "이메일을 입력해주세요."
        !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() -> "유효한 이메일을 입력해주세요."
        else -> null
    }

    private fun validateCode(code: String): String? = when {
        code.isBlank() -> "인증 코드를 입력해주세요."
        !CODE_REGEX.matches(code) -> "6자리 숫자를 입력해주세요."
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "비밀번호를 입력해주세요."
        !PASSWORD_REGEX.matches(password) -> "대소문자·숫자·특수문자 포함 8~16자"
        else -> null
    }

    private fun validateName(name: String): String? = when {
        name.isBlank() -> "이름을 입력해주세요."
        !NAME_REGEX.matches(name) -> "이름은 2~16자로 입력해주세요."
        else -> null
    }

    private fun validateBirthYear(birthYear: String): String? = when {
        birthYear.isBlank() -> "출생연도를 입력해주세요."
        !BIRTH_YEAR_REGEX.matches(birthYear) -> "4자리 숫자로 입력해주세요."
        birthYear.toIntOrNull()?.let { year ->
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            year !in 1900..currentYear
        } == true -> "유효한 연도를 입력해주세요."

        else -> null
    }
}