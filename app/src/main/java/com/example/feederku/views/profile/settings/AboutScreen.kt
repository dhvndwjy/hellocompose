package com.example.feederku.views.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.DMSans
import com.example.feederku.ui.theme.creamBackground
import com.example.feederku.ui.theme.textBrown
import com.example.feederku.views.profile.settings.components.SettingsRow
import com.example.feederku.views.profile.settings.components.SettingsSectionHeader
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "About FeederKu",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFE57373),
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = "Feederku",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textBrown,
                fontFamily = DMSans
            )

            Text(
                text = "Version 3.4.1 Build 2409",
                fontSize = 13.sp,
                color = textBrown.copy(alpha = 0.6f),
                fontFamily = DMSans
            )

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsSectionHeader("APP INFO")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsRow(
                        title = "Version",
                        value = "3.4.1",
                        onClick = {}
                    )
                    SettingsRow(
                        title = "Release Date",
                        value = "Sep 20, 26",
                        onClick = {}
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsSectionHeader("SOCIAL")
                Column(
                    modifier = Modifier.padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsRow(
                        title = "Follow on X",
                        onClick = {}
                    )
                    SettingsRow(
                        title = "Follow on Instagram",
                        onClick = {}
                    )
                }
            }
        }
    }
}
