package com.example.squarednews.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class Article(
    @field:Json(name = "author")
    val author: String? = "",
    @field:Json(name = "authors")
    val authors: String? = "",
    @field:Json(name = "clean_url")
    val cleanUrl: String? = "",
    @field:Json(name = "country")
    val country: String? = "",
    @field:Json(name = "excerpt")
    val excerpt: String? = "",
    @PrimaryKey
    @field:Json(name = "_id")
    val id: String = "",
    @field:Json(name = "is_opinion")
    val isOpinion: Boolean? = false,
    @field:Json(name = "language")
    val language: String? = "",
    @field:Json(name = "link")
    val link: String? = "",
    @field:Json(name = "media")
    val media: String? = "",
    @field:Json(name = "published_date")
    val publishedDate: String? = "",
    @field:Json(name = "published_date_precision")
    val publishedDatePrecision: String? = "",
    @field:Json(name = "rank")
    val rank: Int? = 0,
    @field:Json(name = "rights")
    val rights: String? = "",
    @field:Json(name = "_score")
    val score: Int? = 0,
    @field:Json(name = "summary")
    val summary: String? = "",
    @field:Json(name = "title")
    val title: String? = "",
    @field:Json(name = "topic")
    val topic: String? = "",
    @field:Json(name = "twitter_account")
    val twitterAccount: String? = ""
)