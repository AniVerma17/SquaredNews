package com.example.squarednews.data


import com.squareup.moshi.Json

data class UserInput(
    @Json(name = "countries")
    val countries: List<String> = listOf(),
    @Json(name = "from")
    val from: String = "",
    @Json(name = "lang")
    val lang: Any = Any(),
    @Json(name = "not_countries")
    val notCountries: Any = Any(),
    @Json(name = "not_lang")
    val notLang: Any = Any(),
    @Json(name = "not_sources")
    val notSources: Any = Any(),
    @Json(name = "page")
    val page: Int = 0,
    @Json(name = "size")
    val size: Int = 0,
    @Json(name = "sources")
    val sources: List<String> = listOf(),
    @Json(name = "topic")
    val topic: Any = Any()
)