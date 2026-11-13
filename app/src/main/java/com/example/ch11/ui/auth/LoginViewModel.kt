package com.example.ch11.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch11.data.auth.AuthRepository
import com.example.ch11.data.auth.toAuthMessage
import com.example.ch11.data.model.AuthUser
import com.example.ch11.util.validateLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Tidak ada isSuccess: setelah login berhasil, authState berubah dan
// CatatanKuApp yang memindahkan layar (navigasi = fungsi dari state).
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, errorMessage = null) }

    fun login() {
        if (_uiState.value.isLoading) return                       // cegah klik ganda
        val s = _uiState.value
        val error = validateLogin(s.email, s.password)             // dari util/Validation.kt
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.login(s.email.trim(), s.password)
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.toAuthMessage()) } // dari AuthErrors.kt
                }
            // Sukses: tidak ada isSuccess. authState berubah -> CatatanKuApp yang pindah layar.
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // Tugas 5: token didapat Route dari Credential Manager (butuh Activity)
    fun onGoogleIdToken(idToken: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken)
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.toAuthMessage()) } }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // message null = pengguna menutup dialog akun Google; bukan error
    fun onGoogleSignInFailed(message: String?) {
        if (message != null) _uiState.update { it.copy(errorMessage = message) }
    }

    // Sudah disediakan: isLoading -> panggil repository -> isLoading false + pesan error
    private fun authenticate(block: suspend () -> Result<AuthUser>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = block()
            _uiState.update {
                it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }
}
