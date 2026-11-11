package com.example.feederku.views.livestream.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.feederku.ui.theme.buttonGreen

@Composable
fun LiveChatInput(
    onSendComment: (String) -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }

    val sendAction = {
        if (commentText.isNotBlank()) {
            onSendComment(commentText)
            commentText = ""
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { Text("Say something...", color = Color.White.copy(alpha = 0.6f)) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { sendAction() }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttonGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        // Tombol Like / Love (Heart) untuk spam love
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier
                .background(Color.Red.copy(alpha = 0.8f), shape = RoundedCornerShape(24.dp))
                .size(48.dp)
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Send Love", tint = Color.White)
        }
    }
}
