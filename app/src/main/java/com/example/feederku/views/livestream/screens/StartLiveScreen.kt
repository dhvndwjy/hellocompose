package com.example.feederku.views.livestream.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.feederku.ui.theme.*

@Composable
fun StartLiveScreen(
    onStartClick: (String, String, String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Feeding") }

    // State untuk kontrol kamera & mic
    var isVideoEnabled by remember { mutableStateOf(true) }
    var isAudioEnabled by remember { mutableStateOf(true) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true &&
                              permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        // 1. Top Bar Header Cokelat Gelap
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(textBrown)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Start a Live Stream",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = DMSans
            )
        }

        // 2. Konten Utama
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pratinjau Kamera (CameraX PreviewView)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (hasCameraPermission && isVideoEnabled) {
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
                                        if (isVideoEnabled) {
                                            cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                cameraSelector,
                                                preview
                                            )
                                        }
                                    } catch (exc: Exception) {
                                        // Log exception if needed
                                    }
                                }, ContextCompat.getMainExecutor(context))
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (!hasCameraPermission) "Camera & Audio permission required" else "Camera is Off",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = DMSans
                            )
                        }
                    }

                    // Tombol kontrol kecil di atas pratinjau (Switch Camera, Video Toggle, Mic Toggle)
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Tombol Switch Camera (Front/Back)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.clickable {
                                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Cameraswitch,
                                contentDescription = "Switch Camera",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp).size(18.dp)
                            )
                        }

                        // 2. Tombol Video On/Off
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.clickable {
                                isVideoEnabled = !isVideoEnabled
                            }
                        ) {
                            Icon(
                                if (isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Toggle Video",
                                tint = if (isVideoEnabled) Color.White else Color.Red,
                                modifier = Modifier.padding(8.dp).size(18.dp)
                            )
                        }

                        // 3. Tombol Audio / Mic On/Off
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.clickable {
                                isAudioEnabled = !isAudioEnabled
                            }
                        ) {
                            Icon(
                                if (isAudioEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Toggle Audio",
                                tint = if (isAudioEnabled) Color.White else Color.Red,
                                modifier = Modifier.padding(8.dp).size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Title with Placeholder
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Add Title") },
                    placeholder = { Text("Feeding stray cats behind the local market") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttonGreen,
                        unfocusedBorderColor = textBrown.copy(alpha = 0.3f),
                        focusedLabelColor = buttonGreen,
                        unfocusedLabelColor = textBrown,
                        focusedTextColor = textBrown,
                        unfocusedTextColor = textBrown,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Input Description with Placeholder
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Add Description") },
                    placeholder = { Text("Come and join me while I feed some stray cats...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttonGreen,
                        unfocusedBorderColor = textBrown.copy(alpha = 0.5f),
                        focusedLabelColor = buttonGreen,
                        unfocusedLabelColor = textBrown,
                        focusedTextColor = textBrown,
                        unfocusedTextColor = textBrown.copy(alpha = 0.6f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Kategori Selector
                Text(
                    text = "Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textBrown,
                    fontFamily = DMSans
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Feeding", "Rescue", "Community").forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = buttonGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = textBrown
                            )
                        )
                    }
                }
            }

            // 3. Tombol Aksi di Bawah
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        // Jika title kosong saat diklik start, gunakan placeholder sebagai default
                        val finalTitle = title.ifBlank { "Feeding stray cats behind the local market" }
                        val finalDesc = description.ifBlank { "Come and join me while I feed some stray cats and make sure they are healthy. Doing live check!" }
                        onStartClick(finalTitle, finalDesc, selectedCategory) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Start Streaming", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, color = textBrown, fontSize = 16.sp)
                }
            }
        }
    }
}
