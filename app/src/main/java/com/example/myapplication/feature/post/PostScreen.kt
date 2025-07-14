package com.example.myapplication.feature.post

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.example.myapplication.R
import com.example.myapplication.ui.theme.grey
import com.example.myapplication.ui.theme.white
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

@Composable
internal fun PostRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var backgroundImageUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // 텍스트 추출을 위한 갤러리 launcher
    val textExtractionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // 배경 이미지를 위한 갤러리 launcher
    val backgroundImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        backgroundImageUri = uri
    }

    // 텍스트 추출 이미지가 선택되면 텍스트 인식 실행
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            isProcessing = true

            try {
                val inputImage = InputImage.fromFilePath(context, uri)
                recognizeText(inputImage) { result ->
                    recognizedText = result
                    isProcessing = false
                }
            } catch (e: Exception) {
                isProcessing = false
            }
        }
    }

    // 배경 이미지 로딩
    val backgroundBitmap by produceState<Bitmap?>(null, backgroundImageUri) {
        value = backgroundImageUri?.let { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                bitmap
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // 배경 이미지
        backgroundBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Background image",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
                contentScale = ContentScale.Crop
            )
        }

        AddBookContent(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomEnd))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            TextButton(onClick = {}) {
                Text("완료")
            }
            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { textExtractionLauncher.launch("image/*") },
                enabled = !isProcessing
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_recognize), contentDescription = null,
                    tint = white
                )
            }
            IconButton(
                onClick = { backgroundImageLauncher.launch("image/*") }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_image), contentDescription = null,
                    tint = white
                )
            }
            IconButton(
                onClick = { /* box 센터에 edit text 생성 */ },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_title), contentDescription = null,
                    tint = white
                )
            }
            Spacer(modifier = Modifier.height(56.dp))
        }

        Text(
            text = recognizedText,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

private fun recognizeText(image: InputImage, onResult: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val result = processTextBlock(visionText)
            onResult(if (result.isEmpty()) "텍스트를 찾을 수 없습니다" else result)
        }
        .addOnFailureListener { e ->
            onResult("텍스트 인식 실패: ${e.message}")
        }
}

private fun processTextBlock(result: Text): String {
    return result.text
}

@Composable
fun AddBookContent(
    modifier: Modifier,
) {
    Surface(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x001C1B1F), Color(0xFF1C1B1F)), // 시작 색과 끝 색
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            ), color = Color.Transparent
    ) {
        Row(
            modifier = modifier
                .padding(16.dp)
                .padding(end = 80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_post), contentDescription = null,
                    tint = white
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "책 정보 추가",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = white
            )
        }
    }
}