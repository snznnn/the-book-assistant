package com.example.thebookassistant.api

import com.example.thebookassistant.api.library.OpenLibrarySearchApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"

    val openLibrarySearchApiService: OpenLibrarySearchApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_LIBRARY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibrarySearchApiService::class.java)
    }

}