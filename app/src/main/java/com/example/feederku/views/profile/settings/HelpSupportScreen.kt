package com.example.feederku.views.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feederku.ui.theme.creamBackground
import com.example.feederku.views.profile.settings.components.SettingsRow
import com.example.feederku.views.profile.settings.components.SettingsSectionHeader
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "Help & Support",
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
                SettingsSectionHeader("CONTACT US")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsRow(
                        title = "Email Support",
                        icon = Icons.Default.Email,
                        onClick = {}
                    )
                    SettingsRow(
                        title = "Report Bug",
                        icon = Icons.Default.BugReport,
                        onClick = {}
                    )
                }
            }

            item {
                SettingsSectionHeader("RESOURCES")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsRow(
                        title = "Help Center",
                        icon = Icons.AutoMirrored.Filled.HelpCenter,
                        onClick = {}
                    )
                }
            }

            item {
                SettingsSectionHeader("FREQUENTLY ASKED")
                Column(
                    modifier = Modifier.padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsRow(
                        title = "How do i curate my news feed?",
                        onClick = {}
                    )
                    SettingsRow(
                        title = "Can i share articles to social media?",
                        onClick = {}
                    )
                    SettingsRow(
                        title = "How do i report inaccurate content?",
                        onClick = {}
                    )
                }
            }
        }
    }
}
