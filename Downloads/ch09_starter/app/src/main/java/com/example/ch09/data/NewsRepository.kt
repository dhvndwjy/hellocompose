package com.example.ch09.data

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.local.ArticleEntity
import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.mapper.toEntity
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.model.OfflineInfo
import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.NewsResponseDto
import kotlin.coroutines.cancellation.CancellationException

interface NewsRepository {
    suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed>
    suspend fun searchNews(query: String): Result<NewsFeed>
    suspend fun getArticle(url: String): Article?
}

// TODO [T4.5] Tambahkan parameter `dao: ArticleDao` dan
// `clock: () -> Long = System::currentTimeMillis` (waktu cache, mudah diuji).
class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val dao: ArticleDao,
    private val clock: () -> Long = System::currentTimeMillis
) : NewsRepository {

    override suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed> {
        val categoryKey = category.apiValue
        return fetchAndCache(
            categoryKey = categoryKey,
            remoteCall = { api.getTopHeadlines(category = categoryKey) },
            loadCached = { dao.getArticles(categoryKey) }
        )
    }

    override suspend fun searchNews(query: String): Result<NewsFeed> {
        val searchCategoryKey = "__search__"
        return fetchAndCache(
            categoryKey = searchCategoryKey,
            remoteCall = { api.searchNews(query = query) },
            loadCached = { dao.searchCached(query) }
        )
    }

    override suspend fun getArticle(url: String): Article? {
        return dao.getArticleByUrl(url)?.toDomain()
    }

    private suspend fun fetchAndCache(
        categoryKey: String,
        remoteCall: suspend () -> NewsResponseDto,
        loadCached: suspend () -> List<ArticleEntity>
    ): Result<NewsFeed> {
        val result = runCatching {
            remoteCall().articles
                .mapNotNull { it.toDomain() }
                .distinctBy { it.url }
        }

        // CancellationException BUKAN error jaringan: lempar ulang
        val exception = result.exceptionOrNull()
        if (exception is CancellationException) {
            throw exception
        }

        return if (result.isSuccess) {
            val articles = result.getOrThrow()
            val now = clock()
            val entities = articles.mapIndexed { index, article ->
                article.toEntity(
                    category = categoryKey,
                    position = index,
                    cachedAt = now
                )
            }

            dao.replaceCategory(categoryKey, entities)
            Result.success(NewsFeed(articles = articles, offline = null))
        } else {
            val cachedEntities = loadCached()
            if (cachedEntities.isNotEmpty()) {
                val cachedArticles = cachedEntities.map { it.toDomain() }
                val cachedAt = cachedEntities.maxOf { it.cachedAt }
                val reason = exception?.toUserMessage() ?: "Terjadi kesalahan"

                Result.success(
                    NewsFeed(
                        articles = cachedArticles,
                        offline = OfflineInfo(cachedAt = cachedAt, reason = reason)
                    )
                )
            } else {
                Result.failure(exception ?: Exception("Gagal memuat data"))
            }
        }
    }
}