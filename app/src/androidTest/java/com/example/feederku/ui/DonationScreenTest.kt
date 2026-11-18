package com.example.feederku.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DonationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDonationScreenRenders() {
        composeTestRule.setContent {
            // Panggil composable screen kedua kamu di sini (misal: DonationScreen())
        }
        // Tambahkan assertion sederhana jika perlu
    }
}