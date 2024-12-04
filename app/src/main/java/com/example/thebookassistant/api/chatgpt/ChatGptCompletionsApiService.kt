package com.example.thebookassistant.api.chatgpt

import com.example.thebookassistant.api.chatgpt.model.ChatGptApiRequest
import com.example.thebookassistant.api.chatgpt.model.ChatGptApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGptCompletionsApiService {

    @POST("completions")
    fun completions(
        @Header("Authorization") bearerToken: String,
        @Body request: ChatGptApiRequest
    ): Call<ChatGptApiResponse>

}