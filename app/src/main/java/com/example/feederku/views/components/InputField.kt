package com.example.feederku.views.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.buttonGreen
import com.example.feederku.ui.theme.textBrown

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
){
    // Password visibility toggle state
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ){
        Text( // Label text (Full Name, Email Address, Password)
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textBrown
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 13.sp) },
            singleLine = true,
            isError = errorMessage != null,
            supportingText = { if (errorMessage != null) Text(errorMessage) },
            visualTransformation =
                if (isPassword && !passwordVisible) PasswordVisualTransformation()
                    else VisualTransformation.None,
            trailingIcon = {
                if (isPassword){
                    IconButton(onClick = { passwordVisible = !passwordVisible }){
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password"
                                else "Show password"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password
                else keyboardType,
                imeAction = imeAction
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttonGreen,
                unfocusedBorderColor = textBrown.copy(alpha = 0.15f),
                cursorColor = buttonGreen,
                focusedTextColor = textBrown,
                unfocusedTextColor = textBrown,
                errorTextColor = textBrown,
                focusedPlaceholderColor = textBrown.copy(alpha = 0.4f),
                unfocusedPlaceholderColor = textBrown.copy(alpha = 0.4f)
            )
        )
    }
}
