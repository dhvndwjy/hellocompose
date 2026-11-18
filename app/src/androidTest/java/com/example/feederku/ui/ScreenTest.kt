package com.example.feederku.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAuthScreenOrLoginComponent() {
        // Screen 1: Menguji komponen atau teks yang ada di screen Auth / Login / OnBoarding
        composeTestRule.setContent {
            // Panggil Composable Screen pertama Anda di sini, contoh:
            // LoginScreen() atau biarkan placeholder teks yang ada di app
        }

        // Contoh assertion mencari teks yang ada di UI:
        // composeTestRule.onNodeWithText("Login").assertExists()
    }

    @Test
    fun testDashboardOrDonationScreenComponent() {
        // Screen 2: Menguji screen kedua yang berbeda
        composeTestRule.setContent {
            // Panggil Composable Screen kedua Anda di sini, contoh:
            // DonationScreen()
        }

        // Contoh assertion:
        // composeTestRule.onNodeWithText("FeederKu").assertExists()
    }
}