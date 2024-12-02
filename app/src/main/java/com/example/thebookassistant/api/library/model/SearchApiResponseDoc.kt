package com.example.thebookassistant.api.library.model

data class SearchApiResponseDoc(

    val title: String,
    val author_name: List<String>,
    val key: String

)