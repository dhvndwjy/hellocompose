package com.example.ch10.ui.news

import com.example.ch10.data.NewsRepository
import com.example.ch10.data.model.NewsCategory
import com.example.ch10.data.model.NewsFeed
import com.example.ch10.testutil.MainDispatcherRule
import com.example.ch10.testutil.article
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

// Tugas 3: uji NewsViewModel dengan MockK (bukan FakeNewsRepository).
//  - coEvery { repository.getTopHeadlines(any()) } returns Result.success(...)
//  - buat ViewModel: NewsViewModel(repository) -> init langsung memuat headline
//  - runCurrent() agar coroutine di viewModelScope selesai
//  - periksa viewModel.uiState.value, dan coVerify { ... } untuk jumlah panggilan
// Helper article(id) ada di testutil/TestData.kt. Hilt TIDAK diperlukan di sini.
@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelMockKTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private lateinit var repository: NewsRepository

    @Before fun setUp() {
        repository = mockk()
    }

    // TODO [T3.1] Skenario sukses: repository mengembalikan 2 artikel ->
    //  isLoading false, articles.size == 2, errorMessage null,
    //  dan getTopHeadlines(NewsCategory.General) dipanggil tepat 1 kali.
    @Test fun `sukses - headline mengisi daftar artikel`() = runTest {
        val sampleArticles = listOf(article(1), article(2))
        val sampleFeed = NewsFeed(articles = sampleArticles)

        coEvery { repository.getTopHeadlines(NewsCategory.General) } returns Result.success(sampleFeed)

        val viewModel = NewsViewModel(repository)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.articles.size)
        assertNull(state.errorMessage)

        coVerify(exactly = 1) { repository.getTopHeadlines(NewsCategory.General)}
    }
    
    @Test fun `gagal - pesan error tampil dan daftar kosong`() = runTest {
        coEvery { repository.getTopHeadlines(NewsCategory.General) } returns Result.failure(
            IOException("Network Error")
        )

        val viewModel = NewsViewModel(repository)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.articles.isEmpty())
        assertEquals("Tidak ada koneksi internet.", state.errorMessage)

        coVerify(exactly = 1) { repository.getTopHeadlines(NewsCategory.General) }
    }
}
