package com.example.squarednews.data


import com.squareup.moshi.Json

data class UserInputX(
    @Json(name = "countries")
    val countries: List<String> = listOf(),
    @Json(name = "lang")
    val lang: List<String> = listOf(),
    @Json(name = "topic")
    val topic: String = ""
)