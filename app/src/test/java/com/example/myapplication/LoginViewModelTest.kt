package com.example.myapplication

import app.cash.turbine.test
import com.example.myapplication.feature.login.LoginViewModel
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun `이메일 유효성 검사 테스트`() = runTest {
        val viewModel = LoginViewModel()

        viewModel.container.stateFlow.test {
            // initial state
            awaitItem()

            // when
            viewModel.onEmailChanged("invalid-email")

            // then
            val state = awaitItem()
            assertEquals("invalid-email", state.email)
            assertEquals("유효한 이메일을 입력해주세요.", state.emailError)
        }
    }

    @Test
    fun `비밀번호 유효성 검사 테스트`() = runTest {
        val viewModel = LoginViewModel()

        viewModel.container.stateFlow.test {
            awaitItem()
            viewModel.onPasswordChanged("short")

            val state = awaitItem()
            assertEquals("short", state.password)
            assertEquals("대소문자·숫자·특수문자 포함 8~16자", state.passwordError)
        }
    }

    @Test
    fun `빈 자격증명 로그인 시 에러 발생`() = runTest {
        val viewModel = LoginViewModel()

        viewModel.container.stateFlow.test {
            awaitItem()
            viewModel.onLoginClicked()

            val state = awaitItem()
            assertEquals("이메일을 입력해주세요.", state.emailError)
            assertEquals("비밀번호를 입력해주세요.", state.passwordError)
        }

        viewModel.container.sideEffectFlow.test {
            val effect = awaitItem()
            assert(effect is com.example.myapplication.feature.login.LoginSideEffect.ShowError)
        }
    }
}
