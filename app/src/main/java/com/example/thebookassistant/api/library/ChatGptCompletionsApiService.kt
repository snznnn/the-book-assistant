package com.example.thebookassistant.api.library

import com.example.thebookassistant.api.library.model.ChatGptApiRequest
import com.example.thebookassistant.api.library.model.ChatGptApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatGptCompletionsApiService {

    @POST("completions")
    fun completions(
        @Header("Authorization") bearerToken: String,
        @Body request: ChatGptApiRequest
    ): Call<ChatGptApiResponse>

}