package com.example.feederku.views.auth.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feederku.ui.theme.textBrown
import com.example.feederku.views.auth.AuthViewModel
import com.example.feederku.views.auth.getGoogleIdToken
import com.example.feederku.views.auth.model.AuthUiState
import com.example.feederku.views.components.AuthLayout
import com.example.feederku.views.components.AuthSwitchText
import com.example.feederku.views.components.InputField
import com.example.feederku.views.components.MainButton
import com.example.feederku.views.components.SocialLoginButtons
import kotlinx.coroutines.launch

@Composable
fun SignUpRoute(
    onNavigateToLogin: () -> Unit,
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    onLoginClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.isSignUpSuccess){
        if(uiState.isSignUpSuccess){
            android.widget.Toast.makeText(
                context,
                "Account created successfully! Please login.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            onNavigateToLogin()
        }
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    SignUpScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignUpClick = { viewModel.onSignUpClick() },
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onGoogleClick = {
            scope.launch {
                getGoogleIdToken(context)
                    .onSuccess(viewModel::onGoogleIdToken)
                    .onFailure { 
                        val errorMsg = it.message ?: "Google login failed"
                        viewModel.onGoogleError(errorMsg)
                        if (errorMsg != "Login cancelled") {
                            android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
            }
        },
        modifier = modifier
    )
}

@Composable
fun SignUpScreen(
    uiState: AuthUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AuthLayout(
        onBackClick = onBackClick,
        modifier = modifier,
        bottomContent = {
            MainButton(
                text = "Sign Up",
                onClick = onSignUpClick,
                enabled = uiState.canSignUp,
                isLoading = uiState.isLoading
            )
            SocialLoginButtons(
                dividerText = "Or Sign up with",
                onGoogleClick = onGoogleClick
            )
            AuthSwitchText(
                question = "Already have an account?",
                linkText = "Login",
                onLinkClick = onLoginClick
            )
        }
    ) {
        Text(
            text = "Create Account",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textBrown
        )
        Text(
            text = "Join FeederKu community and protect helpless stray cats.",
            fontSize = 12.sp,
            color = textBrown
        )
        Spacer(Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InputField(
                label = "Full Name",
                value = uiState.name,
                onValueChange = onNameChange,
                placeholder = "e.g. Zayn Malik",
                keyboardType = KeyboardType.Text
            )
            InputField(
                label = "Email Address",
                value = uiState.email,
                onValueChange = onEmailChange,
                placeholder = "e.g. zayn@example.com",
                keyboardType = KeyboardType.Email,
                errorMessage = if (uiState.email.isNotEmpty() && !uiState.isEmailValid) {
                    "Invalid email format"
                } else null
            )
            InputField(
                label = "Password",
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = "Minimum 8 character",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                imeAction = ImeAction.Done
            )

            uiState.errorMessage?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error
                )
            }
        }
    }
}