package com.example.squarednews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.squarednews.data.Article
import com.example.squarednews.data.NewsRepository
import com.example.squarednews.data.preferences.UserPreferencesRepository
import com.example.squarednews.domain.CheckNetworkConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val checkNetworkConnectionUseCase: CheckNetworkConnectionUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private val _isNetworkConnected: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isNetworkConnected: StateFlow<Boolean> = _isNetworkConnected.asStateFlow()

    private val _availableSources: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val availableSources: StateFlow<Set<String>> = _availableSources.asStateFlow()

    val selectedCountry: StateFlow<String> = userPreferencesRepository.getCountry()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "IN")
    private var selectedSources: MutableSet<String> = mutableSetOf()

    var newsHeadlines: Flow<PagingData<Article>> = newsRepository.getNewsHeadlines(selectedCountry.value, selectedSources)
        .flow.cachedIn(viewModelScope)

    private val _searchResults: MutableStateFlow<List<Article>> = MutableStateFlow(emptyList())
    val searchResults: StateFlow<List<Article>> = _searchResults.asStateFlow()

    lateinit var articleToDisplay: Article

    private var searchJob: Job? = null

    fun getAllNewsSources() {
        viewModelScope.launch {
            if (checkNetworkConnectionUseCase()) {
                _isNetworkConnected.emit(true)

                newsRepository.getSources(selectedCountry.value)
            } else {
                _isNetworkConnected.emit(false)
            }
        }
    }

    fun refreshNewsFeed() {
        newsHeadlines = newsRepository.getNewsHeadlines(selectedCountry.value, selectedSources)
            .flow.cachedIn(viewModelScope)
    }

    fun searchNews(q: String) {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
        }
        if (checkNetworkConnectionUseCase()) {
            searchJob = viewModelScope.launch {
                delay(2000) // Change to 1000ms later
                if (isActive) {
                    delay(1000)
                    newsRepository.getNewsBySearchQuery(q)
                }
            }
        }
    }

    fun setSelectedCountry(countryCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setCountry(countryCode)
        }
    }
}