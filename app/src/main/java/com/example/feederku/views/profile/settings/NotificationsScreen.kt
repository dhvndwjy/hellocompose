package com.example.feederku.views.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feederku.ui.theme.creamBackground
import com.example.feederku.views.profile.settings.components.SettingsSectionHeader
import com.example.feederku.views.profile.settings.components.SettingsSwitchRow
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
) {
    var pushNotif by remember { mutableStateOf(true) }
    var emailNotif by remember { mutableStateOf(true) }
    var urgentCare by remember { mutableStateOf(true) }
    var comments by remember { mutableStateOf(true) }
    var likes by remember { mutableStateOf(true) }
    var chats by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "Notifications",
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
                SettingsSectionHeader("CHANNELS")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsSwitchRow(
                        title = "Push Notifications",
                        icon = Icons.Default.Notifications,
                        checked = pushNotif,
                        onCheckedChange = { pushNotif = it }
                    )
                    SettingsSwitchRow(
                        title = "Email Notifications",
                        icon = Icons.Default.Email,
                        checked = emailNotif,
                        onCheckedChange = { emailNotif = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("CONTENT ALERTS")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsSwitchRow(
                        title = "Urgent care needed",
                        icon = Icons.Default.Warning,
                        checked = urgentCare,
                        onCheckedChange = { urgentCare = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("SOCIAL")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsSwitchRow(
                        title = "Comments",
                        icon = Icons.Default.Chat,
                        checked = comments,
                        onCheckedChange = { comments = it }
                    )
                    SettingsSwitchRow(
                        title = "Likes",
                        icon = Icons.Default.Favorite,
                        checked = likes,
                        onCheckedChange = { likes = it }
                    )
                    SettingsSwitchRow(
                        title = "Chats",
                        icon = Icons.Default.Chat,
                        checked = chats,
                        onCheckedChange = { chats = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("SOUND & HAPTIC")
                Column(
                    modifier = Modifier.padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsSwitchRow(
                        title = "Sound",
                        icon = Icons.Default.Notifications,
                        checked = sound,
                        onCheckedChange = { sound = it }
                    )
                    SettingsSwitchRow(
                        title = "Vibration",
                        icon = Icons.Default.Notifications,
                        checked = vibration,
                        onCheckedChange = { vibration = it }
                    )
                }
            }
        }
    }
}
