package com.example.ch10

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch10.ui.detail.ArticleDetailRoute
import com.example.ch10.ui.news.NewsRoute
@Composable
fun KabarKiniApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Routes.News.route
    ) {
        composable(Routes.News.route) {
            NewsRoute(
                onArticleClick = { article ->
                    navController.navigate(Routes.Detail.createRoute(article.url))
                }
            )
        }

        composable(
            route = Routes.Detail.route,
            arguments = listOf(navArgument(ARG_ARTICLE_URL) { type = NavType.StringType })
        ) {
            // articleUrl dibaca ArticleDetailViewModel dari SavedStateHandle
            ArticleDetailRoute(
                onNavigateBack = { navController.popBackStack() },
                onReadMore = { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
    }
}
