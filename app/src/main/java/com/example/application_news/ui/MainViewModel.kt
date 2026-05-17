// ui/MainViewModel.kt
package com.example.application_news.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application_news.BuildConfig
import com.example.application_news.data.Article
import com.example.application_news.data.NetworkModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val articles: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

class MainViewModel : ViewModel() {

    private val _uiState = mutableStateOf<NewsUiState>(NewsUiState.Loading)
    val uiState: State<NewsUiState> = _uiState

    private var isRefreshing = false

    init {
        fetchNews()
    }

    fun fetchNews() {
        if (isRefreshing) return
        isRefreshing = true

        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading

            // Имитация задержки для UX
            delay(500)

            try {
                val apiKey = BuildConfig.NEWS_API_KEY
                if (apiKey.isEmpty()) {
                    _uiState.value = NewsUiState.Error("API Key not configured. Please add NEWS_API_KEY to local.properties")
                    isRefreshing = false
                    return@launch
                }

                val response = NetworkModule.api.getTopHeadlines(
                    country = "us", // Можно изменить на "ru" для российских новостей
                    apiKey = apiKey
                )

                if (response.status == "ok") {
                    // Фильтруем новости без заголовка и картинки для лучшего UX
                    val validArticles = response.articles.filter {
                        it.title.isNotBlank() && it.urlToImage != null
                    }
                    _uiState.value = NewsUiState.Success(validArticles)
                } else {
                    _uiState.value = NewsUiState.Error("API Error: ${response.status}")
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "No internet connection. Please check your network."
                    e.message?.contains("timeout") == true ->
                        "Connection timeout. Please try again."
                    else ->
                        "Error: ${e.message ?: "Unknown error"}"
                }
                _uiState.value = NewsUiState.Error(errorMessage)
            } finally {
                isRefreshing = false
            }
        }
    }

    fun retry() {
        fetchNews()
    }
}