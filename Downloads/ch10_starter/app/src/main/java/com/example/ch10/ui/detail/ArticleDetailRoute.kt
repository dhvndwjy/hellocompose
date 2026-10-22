package com.example.ch10.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ArticleDetailRoute(
    onNavigateBack: () -> Unit,
    onReadMore: (String) -> Unit,
    viewModel: ArticleDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArticleDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onReadMore = onReadMore
    )
}
