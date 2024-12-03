package com.example.thebookassistant.api.library.model

data class ChatGptApiRequest(

    val model: String,
    val chatGptApiRequestMessages: List<ChatGptApiRequestMessage>

)