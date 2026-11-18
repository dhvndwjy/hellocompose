package com.example.feederku.views.auth

import com.example.feederku.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    // Rule wajib agar Coroutine/Dispatchers.Main bisa berjalan aman di pengujian unit (JVM laptop)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun signIn_success_returnsResultSuccess() = runTest {
        // Given (Persiapan: daftarkan akun dulu di fake repository)
        val repository = FakeAuthRepository()
        repository.signUpWithEmail("Dheavanda", "dhea@example.com", "password123")

        // When (Aksi login)
        val result = repository.signInWithEmail("dhea@example.com", "password123")

        // Then (Hasil yang diharapkan: sukses)
        assertTrue(result.isSuccess)
    }

    // Test
    @Test
    fun signIn_unregisteredEmail_returnsError() = runTest {
        // Given
        val repository = FakeAuthRepository()

        // When (Mencoba login dengan email yang belum pernah didaftarkan)
        val result = repository.signInWithEmail("ghost@example.com", "password123")

        // Then (Hasil yang diharapkan: gagal dengan pesan error khusus)
        assertTrue(result.isFailure)
        assertEquals("Account is not registered. Please Sign Up first.", result.exceptionOrNull()?.message)
    }

    @Test
    fun signIn_wrongPassword_returnsError() = runTest {
        // Given
        val repository = FakeAuthRepository()
        repository.signUpWithEmail("Dheavanda", "dhea@example.com", "password123")

        // When (Login dengan password yang salah)
        val result = repository.signInWithEmail("dhea@example.com", "salahpassword")

        // Then (Hasil yang diharapkan: gagal)
        assertTrue(result.isFailure)
        assertEquals("Incorrect email or password.", result.exceptionOrNull()?.message)
    }

    @Test
    fun signUp_success_storesNewUser() = runTest {
        // Given
        val repository = FakeAuthRepository()

        // When (Melakukan registrasi akun baru)
        val result = repository.signUpWithEmail("Dhea", "newuser@example.com", "secure123")

        // Then (Hasil yang diharapkan: sukses)
        assertTrue(result.isSuccess)
    }

    @Test
    fun signUp_duplicateEmail_returnsError() = runTest {
        // Given
        val repository = FakeAuthRepository()
        repository.signUpWithEmail("Dhea", "dhea@example.com", "password123")

        // When (Mendaftar dengan email yang sama persis dua kali)
        val result = repository.signUpWithEmail("Dhea Lain", "dhea@example.com", "password456")

        // Then (Hasil yang diharapkan: gagal karena email sudah terdaftar)
        assertTrue(result.isFailure)
        assertEquals("Email is already registered. Please Login.", result.exceptionOrNull()?.message)
    }
}