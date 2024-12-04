package com.example.thebookassistant.api.openlibrary.model

data class SearchApiResponseDoc(

    val title: String,
    val author_name: List<String>,
    val key: String

)