package com.example.thebookassistant.api

import android.content.Context
import com.example.thebookassistant.api.chatgpt.ChatGptCompletionsApiService
import com.example.thebookassistant.api.openlibrary.OpenLibrarySearchApiService
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"
    private const val CHAT_GPT_BASE_URL = "https://api.openai.com/v1/chat/"

    private fun provideOkHttpClient(context: Context): OkHttpClient {
        val cacheSize = 10L * 1024 * 1024 // 10 mb
        val cache = Cache(context.cacheDir, cacheSize)

        val networkCacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder().header("Cache-Control", "public, max-age=60") // 60 sec
                .removeHeader("Pragma").build()
        }

        return OkHttpClient.Builder().cache(cache).addNetworkInterceptor(networkCacheInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
    }

    fun provideOpenLibrarySearchApiService(context: Context): OpenLibrarySearchApiService {
        return Retrofit.Builder().baseUrl(OPEN_LIBRARY_BASE_URL)
            .client(provideOkHttpClient(context)).addConverterFactory(GsonConverterFactory.create())
            .build().create(OpenLibrarySearchApiService::class.java)
    }

    fun provideChatGptCompletionsApiService(context: Context): ChatGptCompletionsApiService {
        return Retrofit.Builder().baseUrl(CHAT_GPT_BASE_URL).client(provideOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ChatGptCompletionsApiService::class.java)
    }

}