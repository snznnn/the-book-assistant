package com.example.thebookassistant.api.chatgpt.model

data class ChatGptApiRequest(

    val model: String,
    val messages: List<ChatGptApiRequestMessage>

)