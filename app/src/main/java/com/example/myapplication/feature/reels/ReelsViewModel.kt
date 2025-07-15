package com.example.myapplication.feature.reels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.domain.usecase.glim.GetGlimsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val getGlimsUseCase: GetGlimsUseCase,
//    private val toggleLikeUseCase: ToggleLikeUseCase
) : ViewModel(), ContainerHost<ReelsState, ReelsSideEffect> {

    override val container: Container<ReelsState, ReelsSideEffect> = container(ReelsState())

    fun toggleLike() = intent {
        // 낙관적 업데이트
        val updatedGlims = state.glims.map { glim ->
            if (glim.id == state.currentGlimId) {
                val newIsLike = !glim.isLike
                glim.copy(
                    isLike = newIsLike,
                    likes = if (newIsLike) glim.likes + 1 else glim.likes - 1
                )
            } else {
                glim
            }
        }

        reduce { state.copy(glims = updatedGlims) }

        // 서버에 실제 업데이트
        viewModelScope.launch {
//            try {
//                toggleLikeUseCase(glimId).collect { success ->
//                    if (!success) {
//                        // 실패 시 롤백
//                        reduce { state.copy(glims = state.glims) }
//                        postSideEffect(ReelsSideEffect.ShowToast("좋아요 처리에 실패했습니다."))
//                    }
//                }
//            } catch (e: Exception) {
//                // 실패 시 롤백
//                val rolledBackGlims = state.glims.map { glim ->
//                    if (glim.id == glimId) {
//                        currentGlim
//                    } else {
//                        glim
//                    }
//                }
//                reduce { state.copy(glims = rolledBackGlims) }
//                postSideEffect(ReelsSideEffect.ShowToast("좋아요 처리에 실패했습니다."))
//            }
        }
    }

    fun onShareClick() = intent {
        state.currentGlim?.let {
            postSideEffect(ReelsSideEffect.ShareGlim(it))
        }
    }

    fun onCaptureClick(fileName: String) = intent {
        try {
            // 캡처 로직은 외부에서 처리하고 결과만 받음
            postSideEffect(ReelsSideEffect.CaptureSuccess(fileName))
        } catch (e: Exception) {
            postSideEffect(ReelsSideEffect.CaptureError("캡처에 실패했습니다: ${e.message}"))
        }
    }

    fun refresh() = intent {
        reduce { state.copy(glims = emptyList(), currentPage = 0, hasMoreData = true) }
        loadInitialGlims()
    }

    private fun loadInitialGlims() = intent {
        reduce { state.copy(isLoading = true, error = null) }

        try {
            getGlimsUseCase().collect { glims ->
                reduce {
                    state.copy(
                        glims = glims,
                        isLoading = false,
                        hasMoreData = glims.size >= 10
                    )
                }
            }
        } catch (e: Exception) {
            reduce {
                state.copy(
                    isLoading = false,
                    error = "글림을 불러오는데 실패했습니다."
                )
            }
            postSideEffect(ReelsSideEffect.ShowToast("글림을 불러오는데 실패했습니다."))
        }
    }

}
