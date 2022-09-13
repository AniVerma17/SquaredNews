package com.example.squarednews

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.squarednews.data.Article
import com.example.squarednews.data.NewsRepository
import com.example.squarednews.data.SearchResultState
import com.example.squarednews.data.preferences.UserPreferencesRepository
import com.example.squarednews.domain.CheckNetworkConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val checkNetworkConnectionUseCase: CheckNetworkConnectionUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isNetworkConnected: MutableStateFlow<Boolean> = MutableStateFlow(checkNetworkConnectionUseCase())
    val isNetworkConnected: StateFlow<Boolean> = _isNetworkConnected.asStateFlow()

    private val _availableSources: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val availableSources: StateFlow<List<String>> = _availableSources.asStateFlow()

    val selectedCountry: Flow<String> = userPreferencesRepository.getCountry()

    var selectedSources: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())

    var tempSelectedSources: MutableState<Set<String>> = mutableStateOf(emptySet())

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    var newsHeadlines: Flow<PagingData<Article>> =
        selectedCountry.combine(selectedSources) { country, sources -> country to sources }
            .debounce(100)
            .flatMapLatest {
                newsRepository.getNewsHeadlines(it.first, it.second).flow.cachedIn(viewModelScope)
            }

    var searchKeyword: MutableState<String> = mutableStateOf("")

    private val _searchResults: MutableStateFlow<SearchResultState?> = MutableStateFlow(null)
    val searchResults: StateFlow<SearchResultState?> = _searchResults.asStateFlow()

    lateinit var articleToDisplay: Article

    private var searchJob: Job? = null

    fun getAllNewsSources() {
        viewModelScope.launch {
            if (checkNetworkConnectionUseCase()) {
                _isNetworkConnected.emit(true)
                newsRepository.getSources(selectedCountry.first())?.let {
                    _availableSources.emit(it.sources)
                }
            } else {
                _isNetworkConnected.emit(false)
            }
        }
    }

    fun searchNews(q: String) {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
        }
        viewModelScope.launch {
            if (checkNetworkConnectionUseCase()) {
                searchJob = launch {
                    delay(1000)
                    _searchResults.emit(null)
                    if (isActive && q.isNotBlank()) {
                        _searchResults.emit(SearchResultState.Loading)
                        delay(1000)
                        newsRepository.getNewsBySearchQuery(q)?.let {
                            _searchResults.emit(SearchResultState.Success(it.articles))
                        } ?: _searchResults.emit(
                            SearchResultState.Error(SearchResultState.ErrorType.API_ERROR)
                        )
                    }
                }
            } else {
                _searchResults.emit(SearchResultState.Error(SearchResultState.ErrorType.NETWORK_ERROR))
            }
        }
    }

    fun setSelectedCountry(countryCode: String) {
        viewModelScope.launch {
            _availableSources.emit(emptyList())
            selectedSources.emit(emptySet())
            userPreferencesRepository.setCountry(countryCode)
        }
    }

    fun setSourceSelection(source: String, isSelected: Boolean) {
        val set = tempSelectedSources.value.toMutableSet()
        if (isSelected) {
            set.add(source)
        } else {
            set.remove(source)
        }
        tempSelectedSources.value = set.sorted().toSet()
    }

    fun applySelectedSources() {
        viewModelScope.launch { selectedSources.emit(tempSelectedSources.value) }
    }
}