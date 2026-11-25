package com.example.feederku.views.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.feederku.views.profile.settings.components.SettingsRow
import com.example.feederku.views.profile.settings.components.SettingsSectionHeader
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onAccountInfoClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPrivacySecurityClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "Settings",
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
                SettingsSectionHeader("ACCOUNT")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsRow(
                        title = "Account Information",
                        icon = Icons.Default.Person,
                        onClick = onAccountInfoClick
                    )
                    SettingsRow(
                        title = "Edit Profile",
                        icon = Icons.Default.Person,
                        onClick = onEditProfileClick
                    )
                }
            }

            item {
                SettingsSectionHeader("PREFERENCES")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsRow(
                        title = "Notifications",
                        icon = Icons.Default.Notifications,
                        onClick = onNotificationsClick
                    )
                    SettingsRow(
                        title = "Privacy & Security",
                        icon = Icons.Default.Security,
                        onClick = onPrivacySecurityClick
                    )
                    SettingsRow(
                        title = "Change Password",
                        icon = Icons.Default.Lock,
                        onClick = onChangePasswordClick
                    )
                    SettingsRow(
                        title = "Language",
                        icon = Icons.Default.Language,
                        value = "English",
                        onClick = onLanguageClick
                    )
                }
            }

            item {
                SettingsSectionHeader("SUPPORT")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsRow(
                        title = "Help & Support",
                        icon = Icons.Default.HelpOutline,
                        onClick = onHelpSupportClick
                    )
                    SettingsRow(
                        title = "About FeederKu",
                        icon = Icons.Default.Info,
                        onClick = onAboutClick
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
                            imageVector = Icons.Default.ExitToApp,
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
