package com.example.squarednews.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.squarednews.Constants
import com.example.squarednews.data.db.NewsDb
import com.example.squarednews.data.paging.NewsFeedMediator
import java.lang.Exception
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val newsDb: NewsDb,
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getNewsHeadlines(country: String, sources: Set<String> = emptySet()): Pager<Int, Article> {
        val filters = mutableMapOf("countries" to country).apply {
            if (sources.isNotEmpty()) {
                set("sources", sources.joinToString(","))
            }
        }.toMap()
        return Pager(config = PagingConfig(pageSize = Constants.PAGE_SIZE),
            remoteMediator = NewsFeedMediator(newsApiService, newsDb, filters)
        ) {
            newsDb.newsDao().getArticles()
        }
    }

    suspend fun getNewsBySearchQuery(query: String): NewsResponse? = try {
        newsApiService.getSearchResults(query)
    } catch (e: Exception) {
        null
    }

    suspend fun getSources(country: String): SourcesResponse? = try {
        newsApiService.getSources(country)
    } catch (e: Exception) {
        null
    }
}