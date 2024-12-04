package com.example.thebookassistant.api

import com.example.thebookassistant.api.chatgpt.ChatGptCompletionsApiService
import com.example.thebookassistant.api.openlibrary.OpenLibrarySearchApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"
    private const val CHAT_GPT_BASE_URL = "https://api.openai.com/v1/chat/"

    val openLibrarySearchApiService: OpenLibrarySearchApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_LIBRARY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibrarySearchApiService::class.java)
    }

    val chatGptCompletionsApiService: ChatGptCompletionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(CHAT_GPT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGptCompletionsApiService::class.java)
    }

}