package com.example.thebookassistant.api

import com.example.thebookassistant.api.chatgpt.ChatGptCompletionsApiService
import com.example.thebookassistant.api.openlibrary.OpenLibrarySearchApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"
    private const val CHAT_GPT_BASE_URL = "https://api.openai.com/v1/chat/"

    val openLibrarySearchApiService: OpenLibrarySearchApiService by lazy {
        Retrofit.Builder().baseUrl(OPEN_LIBRARY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(OpenLibrarySearchApiService::class.java)
    }

    val chatGptCompletionsApiService: ChatGptCompletionsApiService by lazy {
        val okHttpClient = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build()

        Retrofit.Builder().baseUrl(CHAT_GPT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ChatGptCompletionsApiService::class.java)
    }

}