package com.example.thebookassistant.api.library

import com.example.thebookassistant.api.library.model.SearchApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibrarySearchApiService {

    @GET("search.json")
    fun search(
        @Query("title") title: String,
        @Query("author") author: String,
        @Query("limit") limit: String
    ): Call<SearchApiResponse>

}