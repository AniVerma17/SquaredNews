package com.example.squarednews.data


import com.squareup.moshi.Json

data class NewsResponse(
    @field:Json(name = "articles")
    val articles: List<Article> = listOf(),
    @field:Json(name = "page")
    val page: Int = 0,
    @field:Json(name = "page_size")
    val pageSize: Int = 0,
    @field:Json(name = "status")
    val status: String = "",
    @field:Json(name = "total_hits")
    val totalHits: Int = 0,
    @field:Json(name = "total_pages")
    val totalPages: Int = 0,
    @field:Json(name = "user_input")
    val userInput: UserInput = UserInput()
)