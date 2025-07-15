package com.example.myapplication.feature.post.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.feature.post.TextStyleState

@Composable
fun PostContent(
    recognizedText: String,
    textStyle: TextStyleState,
    backgroundImageUri: Uri?,
    showExitDialog: Boolean,
    isFocused: Boolean,
    isDragging: Boolean,
    offsetX: Float,
    offsetY: Float,
    onTextChanged: (String) -> Unit,
    onTextFocusChanged: (Boolean) -> Unit,
    onBackgroundClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onConfirmExit: () -> Unit,
    onCancelExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalContentColor provides Color.White) {
        MaterialTheme(
            colorScheme = darkColorScheme(),
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes
        ) {
            val focusManager = LocalFocusManager.current

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0x881C1B1F), Color(0xFF1C1B1F)),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onBackgroundClick()
                        focusManager.clearFocus() // 실제 포커스도 해제
                    }
            ) {
                AsyncImage(
                    model = backgroundImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                EditableTextField(
                    text = recognizedText,
                    textStyle = textStyle,
                    isFocused = isFocused,
                    isDragging = isDragging,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    onTextChange = onTextChanged,
                    onFocusChanged = onTextFocusChanged,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd,
                    onDrag = onDrag,
                    onIncreaseFontSize = onIncreaseFontSize,
                    onDecreaseFontSize = onDecreaseFontSize,
                    onToggleBold = onToggleBold,
                    onToggleItalic = onToggleItalic,
                    modifier = Modifier.align(Alignment.Center)
                )

                ActionButtons(
                    onTextExtractionClick = onTextExtractionClick,
                    onBackgroundImageClick = onBackgroundImageClick,
                    onCompleteClick = onCompleteClick,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )

                BookInfoSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                )

                // Exit Dialog
                if (showExitDialog) {
                    AlertDialog(
                        onDismissRequest = onCancelExit,
                        title = { Text("나가시겠습니까?") },
                        text = { Text("작성 중인 내용이 사라집니다.") },
                        confirmButton = {
                            TextButton(
                                onClick = onConfirmExit
                            ) {
                                Text("나가기")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = onCancelExit
                            ) {
                                Text("취소")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookInfoSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .padding(end = 80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.icon_post),
                contentDescription = null
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "책 정보 추가",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}