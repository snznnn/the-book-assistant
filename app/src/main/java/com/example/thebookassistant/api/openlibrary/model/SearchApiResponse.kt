package com.example.thebookassistant.api.openlibrary.model

data class SearchApiResponse(

    val numFound: Int,
    val docs: List<SearchApiResponseDoc>

)