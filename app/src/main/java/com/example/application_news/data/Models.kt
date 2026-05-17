package com.example.application_news.data

import java.text.SimpleDateFormat
import java.util.Locale

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
) {
    // Форматирование даты с использованием SimpleDateFormat
    fun getFormattedDate(): String {
        return try {
            // Входной формат: "2024-01-15T10:30:00Z"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

            val date = inputFormat.parse(publishedAt)
            if (date != null) {
                outputFormat.format(date)
            } else {
                publishedAt.take(10)
            }
        } catch (e: Exception) {
            publishedAt.take(10)
        }
    }
}

data class Source(
    val id: String?,
    val name: String
)