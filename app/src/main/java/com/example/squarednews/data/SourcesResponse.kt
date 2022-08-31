package com.example.squarednews.data


import com.squareup.moshi.Json

data class SourcesResponse(
    @Json(name = "message")
    val message: String = "No sources found",
    @Json(name = "sources")
    val sources: List<String> = listOf(),
    @Json(name = "user_input")
    val userInput: UserInputX = UserInputX()
)