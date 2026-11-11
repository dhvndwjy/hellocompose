package com.example.feederku.views.livestream.screens

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.feederku.views.livestream.model.LiveStreamUiState
import com.example.feederku.views.livestream.components.LiveChatBubble
import com.example.feederku.views.livestream.components.LiveChatInput
import com.example.feederku.views.livestream.components.LiveHeaderOverlay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ActiveLiveScreen(
    uiState: LiveStreamUiState.Active,
    onEndClick: () -> Unit,
    onSendComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State untuk kontrol host (kamera & mic)
    var isVideoEnabled by remember { mutableStateOf(true) }
    var isAudioEnabled by remember { mutableStateOf(true) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }

    // State untuk animasi spam love
    var loveEffects by remember { mutableStateOf(listOf<Int>()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Kamera Preview sebagai Background Full Screen
        if (isVideoEnabled) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = CameraPreview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (_: Exception) {
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
        }

        // 2. Header Overlay (Badge LIVE & Tombol End)
        LiveHeaderOverlay(
            viewerCount = uiState.viewerCount,
            onEndClick = onEndClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // 3. Kontrol Host di bawah tombol End (Kanan Atas: Switch Camera, Video, Mic)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 64.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Switch Camera
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        } else {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // Video On/Off
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { isVideoEnabled = !isVideoEnabled }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Toggle Video",
                        tint = if (isVideoEnabled) Color.White else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Mic On/Off
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { isAudioEnabled = !isAudioEnabled }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (isAudioEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Toggle Mic",
                        tint = if (isAudioEnabled) Color.White else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 4. Animasi Spam Love di pojok kanan bawah
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 120.dp)
                .size(100.dp, 250.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            loveEffects.forEach { id ->
                FloatingHeartItem(onEnd = {
                    loveEffects = loveEffects.filter { it != id }
                })
            }
        }

        // 5. Bagian Bawah: Daftar Komentar & Kolom Input dengan Tombol Spam Love
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                items(uiState.comments.reversed()) { chat ->
                    LiveChatBubble(
                        sender = chat.senderName,
                        message = chat.message
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LiveChatInput(
                onSendComment = onSendComment,
                onLikeClick = {
                    // Tambah efek love baru untuk spam love
                    loveEffects = loveEffects + (System.currentTimeMillis().toInt())
                }
            )
        }
    }
}

@Composable
fun FloatingHeartItem(onEnd: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                offsetY.animateTo(-220f, animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing))
            }
            launch {
                alpha.animateTo(0f, animationSpec = tween(durationMillis = 1500, easing = LinearEasing))
            }
            launch {
                scale.animateTo(1.3f, animationSpec = tween(durationMillis = 500))
            }
        }
        delay(100)
        onEnd()
    }

    Box(
        modifier = Modifier
            .offset(y = offsetY.value.dp)
            .graphicsLayer(alpha = alpha.value, scaleX = scale.value, scaleY = scale.value)
    ) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = "Heart",
            tint = Color.Red,
            modifier = Modifier.size(36.dp)
        )
    }
}
