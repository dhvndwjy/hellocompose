package com.example.feederku.views.profile.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.R
import com.example.feederku.ui.theme.DMSans
import com.example.feederku.ui.theme.boxFitur
import com.example.feederku.ui.theme.buttonGreen
import com.example.feederku.ui.theme.creamBackground
import com.example.feederku.ui.theme.textBrown
import com.example.feederku.views.components.InputField
import com.example.feederku.views.profile.settings.components.SettingsTopBar

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("Sarah Anderson") }
    var username by remember { mutableStateOf("sarahfeeder") }
    var email by remember { mutableStateOf("sarah@example.com") }
    var bio by remember { mutableStateOf("Animal lover | Jakarta stray feeder since 2023 🐾") }
    var phone by remember { mutableStateOf("+62 812-3456-7890") }
    var city by remember { mutableStateOf("Jakarta, Indonesia") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBackground)
    ) {
        SettingsTopBar(
            title = "Edit Profile",
            onBackClick = onBackClick,
            actions = {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DMSans
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = painterResource(R.drawable.profile_circle),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .border(2.dp, buttonGreen, CircleShape)
                        .clip(CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(buttonGreen, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = "Change Profile Photo",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = buttonGreen,
                fontFamily = DMSans
            )

            Spacer(Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InputField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = ""
                )
                InputField(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it },
                    placeholder = ""
                )

                // Email with verified checkmark
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Email Address (Verified)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textBrown,
                        fontFamily = DMSans
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(boxFitur)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = email,
                                fontSize = 14.sp,
                                color = textBrown,
                                fontFamily = DMSans
                            )
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = buttonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                InputField(
                    label = "Bio",
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = ""
                )
                InputField(
                    label = "Phone Number",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = ""
                )

                // Primary Area dropdown style
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Primary Area",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textBrown,
                        fontFamily = DMSans
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(boxFitur)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = city,
                                fontSize = 14.sp,
                                color = textBrown,
                                fontFamily = DMSans
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select",
                                tint = textBrown,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
