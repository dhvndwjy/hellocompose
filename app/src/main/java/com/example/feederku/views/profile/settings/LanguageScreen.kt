package com.example.feederku.views.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.DMSans
import com.example.feederku.ui.theme.boxFitur
import com.example.feederku.ui.theme.buttonGreen
import com.example.feederku.ui.theme.creamBackground
import com.example.feederku.ui.theme.textBrown
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun LanguageScreen(
    onBackClick: () -> Unit
) {
    val languages = listOf(
        Pair("English", "English"),
        Pair("Bahasa Indonesia", "Bahasa Indonesia"),
        Pair("Deutsch", "German"),
        Pair("Chinese", "Chinese")
    )
    var selectedLang by remember { mutableStateOf("English") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "Language",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select your preffered language for the Feederku interface",
                fontSize = 13.sp,
                color = textBrown.copy(alpha = 0.7f),
                fontFamily = DMSans
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                languages.forEach { (lang, sub) ->
                    val isSelected = selectedLang == lang
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(boxFitur)
                            .then(
                                if (isSelected) Modifier.border(1.5.dp, buttonGreen, RoundedCornerShape(14.dp))
                                else Modifier
                            )
                            .clickable { selectedLang = lang }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = lang,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textBrown,
                                fontFamily = DMSans
                            )
                            Text(
                                text = sub,
                                fontSize = 12.sp,
                                color = textBrown.copy(alpha = 0.6f),
                                fontFamily = DMSans
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = buttonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
