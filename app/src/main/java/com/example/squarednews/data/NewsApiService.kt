package com.example.squarednews.data

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface NewsApiService {

    @GET("/v2/latest_headlines")
    suspend fun getHeadlines(@QueryMap filters: Map<String, String>,
                             @Query("page_size") pageSize: Int,
                             @Query("page") pageNumber: Int): NewsResponse

    @GET("/v2/search")
    suspend fun getSearchResults(@Query("q") searchQuery: String): NewsResponse

    @GET("/v2/sources")
    suspend fun getSources(@Query("countries") countries: String): SourcesResponse
}