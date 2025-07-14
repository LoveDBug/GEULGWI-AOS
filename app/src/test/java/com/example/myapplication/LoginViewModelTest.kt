// src/test/java/com/example/myapplication/LoginViewModelTest.kt
package com.example.myapplication

import com.example.myapplication.feature.login.LoginUiState
import com.example.myapplication.feature.login.LoginViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel()
    }

    @Test
    fun `잘못된 이메일 입력 시 emailError가 설정된다`() = runTest {
        val states = mutableListOf<LoginUiState>()
        // 1) stateFlow 구독
        viewModel.container.stateFlow
            .onEach { states += it }
            .launchIn(this)

        // 2) 인텐트 호출
        viewModel.onEmailChanged("invalid-email")

        // 3) 스케줄러 진행
        advanceUntilIdle()

        // 4) 마지막 상태 검증
        val last = states.last()
        assertEquals("invalid-email", last.email)
        assertEquals("유효한 이메일을 입력해주세요.", last.emailError)
    }

    @Test
    fun `비밀번호 입력 시 passwordError가 설정된다`() = runTest {
        val states = mutableListOf<LoginUiState>()
        viewModel.container.stateFlow
            .onEach { states += it }
            .launchIn(this)

        viewModel.onPasswordChanged("short")
        advanceUntilIdle()

        val last = states.last()
        assertEquals("short", last.password)
        assertEquals("대소문자·숫자·특수문자 포함 8~16자", last.passwordError)
    }

    @Test
    fun `빈 자격 증명으로 로그인 클릭 시 상태만 검증한다`() = runTest {
        val states = mutableListOf<LoginUiState>()
        viewModel.container.stateFlow
            .onEach { states += it }
            .launchIn(this)

        // 빈 이메일·비밀번호로 로그인 클릭
        viewModel.onLoginClicked()
        advanceUntilIdle()

        val last = states.last()
        assertEquals("이메일을 입력해주세요.", last.emailError)
        assertEquals("비밀번호를 입력해주세요.", last.passwordError)
    }
}
