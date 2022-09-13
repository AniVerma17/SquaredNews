package com.example.squarednews.data

sealed class SearchResultState {
    object Loading : SearchResultState()
    data class Success(val list: List<Article>) : SearchResultState()
    data class Error(val errorType: ErrorType) : SearchResultState()

    enum class ErrorType {
        NETWORK_ERROR,
        API_ERROR
    }
}
