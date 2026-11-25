package com.example.feederku.views.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.DMSans
import com.example.feederku.ui.theme.buttonGreen
import com.example.feederku.ui.theme.creamBackground
import com.example.feederku.ui.theme.textBrown
import com.example.feederku.views.components.InputField
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onPasswordUpdated: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "Change Password",
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
            Spacer(Modifier.height(12.dp))
            Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = "Choose a strong password with at least 8 characters, one uppercase letter and one number",
                fontSize = 12.sp,
                color = textBrown.copy(alpha = 0.7f),
                fontFamily = DMSans,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InputField(
                    label = "Current Password",
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )
                InputField(
                    label = "New Password",
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )
                InputField(
                    label = "Confirm New Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onPasswordUpdated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGreen)
            ) {
                Text(
                    text = "Update Password",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DMSans
                )
            }
        }
    }
}
