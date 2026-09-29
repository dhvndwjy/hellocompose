package com.example.ch06

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch06.profile.ProfileUiState

// Belum punya ViewModel sendiri — jadi bahan Tantangan Tugas Pertemuan 6.
//
// TODO [T4.1] Buat `profile/ProfileRepository.kt`: data class `UserProfile(username,
//   notificationsEnabled)`, interface `ProfileRepository { fun getProfile(): UserProfile }`
//   (sinkron, TANPA suspend, karena datanya lokal), dan `FakeProfileRepository`.
// TODO [T4.2] Buat `profile/ProfileUiState.kt`: `username` dan `notificationsEnabled` dalam
//   SATU data class, bukan dua mutableStateOf lepas.
// TODO [T4.3] Buat `profile/ProfileViewModel.kt` (StateFlow + update) dengan event
//   `onUsernameChange(String)` dan `onToggleNotification(Boolean)`.
// TODO [T4.4] Buat `profile/ProfileViewModelFactory.kt`. Polanya sama dengan HomeViewModelFactory.
// TODO [T4.5] Buat `profile/ProfileRoute.kt` (satu-satunya yang tahu ViewModel dan Context),
//   lalu pindahkan composable ini ke package `profile` dan ubah menjadi
//   ProfileScreen(uiState, onUsernameChange, onToggleNotification) yang murni presentasional:
//   tampilkan OutlinedTextField nama pengguna dan Switch notifikasi.
//   (T4.6 ada di di/AppContainer.kt, T4.7 ada di MainScreen.kt.)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onUsernameChange: (String) -> Unit,
    onToggleNotification: (Boolean) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Profil") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                label = { Text("Nama pengguna") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "mahasiswa@kampus.ac.id",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (uiState.notificationsEnabled) "Notifikasi aktif"
                    else "Notifikasi nonaktif",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = onToggleNotification
                )
            }
        }
    }
}