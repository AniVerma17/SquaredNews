package com.example.squarednews.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.squarednews.Constants
import com.example.squarednews.data.Article
import com.example.squarednews.data.NewsApiService
import com.example.squarednews.data.db.NewsDao
import com.example.squarednews.data.db.NewsDb
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NewsFeedMediator(
    private val newsApiService: NewsApiService,
    private val newsDb: NewsDb,
    private val filters: Map<String, String>,
) : RemoteMediator<Int, Article>() {
    private val newsDao: NewsDao = newsDb.newsDao()
    private var page: Int = 1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            delay(1000)
            val pageToFetch = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> page + 1
            }
            val apiResponse = newsApiService.getHeadlines(filters, Constants.PAGE_SIZE, pageToFetch)
            newsDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    newsDao.clearAllArticles()
                }
                newsDao.insertAllNews(apiResponse.articles)
            }
            page = apiResponse.page
            MediatorResult.Success(apiResponse.page == apiResponse.totalPages)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH
}