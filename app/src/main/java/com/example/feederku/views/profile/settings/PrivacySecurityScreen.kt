package com.example.feederku.views.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.DMSans
import com.example.feederku.ui.theme.creamBackground
import com.example.feederku.ui.theme.textBrown
import com.example.feederku.views.profile.settings.components.SettingsSectionHeader
import com.example.feederku.views.profile.settings.components.SettingsSwitchRow
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun PrivacySecurityScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var readReceipts by remember { mutableStateOf(true) }
    var activityStatus by remember { mutableStateOf(true) }
    var twoFactor by remember { mutableStateOf(true) }
    var biometricLock by remember { mutableStateOf(true) }
    var downloadData by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "Privacy & Security",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                SettingsSectionHeader("PRIVACY")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsSwitchRow(
                        title = "Read Receipts",
                        icon = Icons.Default.Visibility,
                        checked = readReceipts,
                        onCheckedChange = { readReceipts = it }
                    )
                    SettingsSwitchRow(
                        title = "Activity Status",
                        icon = Icons.Default.Circle,
                        checked = activityStatus,
                        onCheckedChange = { activityStatus = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("SECURITY")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsSwitchRow(
                        title = "Two-Factor Authentication",
                        icon = Icons.Default.Lock,
                        checked = twoFactor,
                        onCheckedChange = { twoFactor = it }
                    )
                    SettingsSwitchRow(
                        title = "Biometric Lock",
                        icon = Icons.Default.Fingerprint,
                        checked = biometricLock,
                        onCheckedChange = { biometricLock = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("DATA")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsSwitchRow(
                        title = "Download My Data",
                        icon = Icons.Default.CloudDownload,
                        checked = downloadData,
                        onCheckedChange = { downloadData = it }
                    )
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            text = "Log Out",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = DMSans
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "FeederKu App • Version 1.4.2",
                    fontSize = 11.sp,
                    color = textBrown.copy(alpha = 0.5f),
                    fontFamily = DMSans,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
