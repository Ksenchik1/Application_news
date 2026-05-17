// data/NewsApiService.kt
package com.example.application_news.data

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {  // ← Это интерфейс, а не аннотация!
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String
    ): NewsResponse
}