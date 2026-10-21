package com.example.ch09.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ch09.MainApplication

@Composable
fun ArticleDetailRoute(
    articleUrl: String,
    onNavigateBack: () -> Unit
) {
    val app = LocalContext.current.applicationContext as MainApplication
    val uriHandler = LocalUriHandler.current

    val viewModel: ArticleDetailViewModel = viewModel(
        key = articleUrl,
        factory = viewModelFactory {
            initializer {
                ArticleDetailViewModel(
                    app.container.newsRepository,
                    articleUrl
                )
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArticleDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onReadMore = {
            uiState.article?.url?.let { url ->
                if (url.isNotBlank()) {
                    uriHandler.openUri(url)
                }
            }
        }
    )
}