package com.example.myapplication.feature.post.component

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.feature.post.TextStyleState
import kotlin.math.roundToInt
// 더 간단한 방식: Box로 감싸서 테두리 영역에 드래그 감지

@Composable
fun EditableTextField(
    text: String,
    textStyle: TextStyleState,
    isFocused: Boolean,
    isDragging: Boolean,
    offsetX: Float,
    offsetY: Float,
    onTextChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            focusRequester.requestFocus()
        } else {
            focusRequester.freeFocus()
        }
    }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .padding(48.dp)
            .border(
                width = 1.dp,
                color = when {
                    isDragging -> Color.Yellow
                    isFocused -> Color.White
                    else -> Color.Transparent
                },
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onDragStart()
                    },
                    onDragEnd = {
                        onDragEnd()
                    }
                ) { change, dragAmount ->
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
    ) {
        Column {
            TextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    fontSize = textStyle.fontSizeUnit,
                    fontWeight = textStyle.fontWeight,
                    fontStyle = textStyle.fontStyle
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                readOnly = isDragging,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    }
            )

            if (isFocused && !isDragging) {
                TextConfigContent(
                    textStyle = textStyle,
                    onIncreaseFontSize = onIncreaseFontSize,
                    onDecreaseFontSize = onDecreaseFontSize,
                    onToggleBold = onToggleBold,
                    onToggleItalic = onToggleItalic
                )
            }

            if (isDragging) {
                Surface(
                    color = Color.Yellow.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "이동 중...",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TextConfigContent(
    textStyle: TextStyleState,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextSizeControls(
                fontSize = textStyle.fontSize,
                onIncreaseFontSize = onIncreaseFontSize,
                onDecreaseFontSize = onDecreaseFontSize
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextStyleControls(
                isBold = textStyle.isBold,
                isItalic = textStyle.isItalic,
                onToggleBold = onToggleBold,
                onToggleItalic = onToggleItalic
            )
        }
    }
}

@Composable
private fun TextSizeControls(
    fontSize: Float,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onDecreaseFontSize,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_image),
                contentDescription = "텍스트 크기 줄이기",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = "${fontSize.toInt()}",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
            onClick = onIncreaseFontSize,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_post),
                contentDescription = "텍스트 크기 키우기",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun TextStyleControls(
    isBold: Boolean,
    isItalic: Boolean,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onToggleBold,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_title),
                contentDescription = "굵게",
                tint = if (isBold) Color.Yellow else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        IconButton(
            onClick = onToggleItalic,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_more),
                contentDescription = "기울이기",
                tint = if (isItalic) Color.Yellow else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ActionButtons(
    onTextExtractionClick: () -> Unit,
    onBackgroundImageButtonClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        TextButton(onClick = onCompleteClick) {
            Text("완료")
        }

        Spacer(modifier = Modifier.weight(1f))

        ActionButton(
            onClick = onTextExtractionClick,
            iconRes = R.drawable.ic_recognize,
            contentDescription = "텍스트 인식"
        )

        ActionButton(
            onClick = onBackgroundImageButtonClick,
            iconRes = R.drawable.ic_image,
            contentDescription = "배경 이미지"
        )

        ActionButton(
            onClick = { /* 새 텍스트 생성 */ },
            iconRes = R.drawable.ic_title,
            contentDescription = "새 텍스트"
        )

        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription
        )
    }
}