package com.example.thebookassistant.api.library.model

data class SearchApiResponse(

    val numFound: Int,
    val docs: List<SearchApiResponseDoc>

)