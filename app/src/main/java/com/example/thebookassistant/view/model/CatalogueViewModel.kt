package com.example.thebookassistant.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thebookassistant.api.openlibrary.OpenLibrarySearchApiService
import com.example.thebookassistant.api.openlibrary.model.SearchApiResponse
import com.example.thebookassistant.api.openlibrary.model.SearchApiResponseDoc
import com.example.thebookassistant.data.dao.FavoritedBooksDao
import com.example.thebookassistant.data.entity.FavoritedBooks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogueViewModel(
    private val favoritedBooksDao: FavoritedBooksDao,
    private val openLibraryService: OpenLibrarySearchApiService
) : ViewModel() {

    val title = MutableStateFlow("")
    val authorName = MutableStateFlow("")
    fun setTitle(value: String) = title.tryEmit(value)
    fun setAuthorName(value: String) = authorName.tryEmit(value)

    val books = MutableStateFlow<List<SearchApiResponseDoc>>(emptyList())
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)
    val noResultsFound = MutableStateFlow(false)

    val favoriteBooks: StateFlow<Set<String>> = MutableStateFlow(emptySet<String>()).also { flow ->
        viewModelScope.launch {
            favoritedBooksDao.getAllFavoriteBooks().collect { favorites ->
                flow.value = favorites.map { it.key }.toSet()
            }
        }
    }

    fun fetchBooks(title: String, author: String) {
        if (title.isBlank() || author.isBlank()) return

        isLoading.value = true
        noResultsFound.value = false
        errorMessage.value = null

        openLibraryService.search(title, author, "5").enqueue(object : Callback<SearchApiResponse> {
            override fun onResponse(
                call: Call<SearchApiResponse>,
                response: Response<SearchApiResponse>
            ) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val fetchedBooks = response.body()?.docs.orEmpty()
                    if (fetchedBooks.isEmpty()) {
                        noResultsFound.value = true
                    } else {
                        books.value = fetchedBooks
                    }
                } else {
                    errorMessage.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<SearchApiResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Failure: ${t.message}"
            }
        })
    }

    fun favoriteBook(book: SearchApiResponseDoc) {
        viewModelScope.launch {
            favoritedBooksDao.insertFavoriteBook(
                FavoritedBooks(
                    title = book.title,
                    authors = book.author_name.joinToString(", "),
                    key = book.key
                )
            )
        }
    }

    fun unfavoriteBook(key: String) {
        viewModelScope.launch {
            favoritedBooksDao.deleteFavoriteBookByKey(key)
        }
    }
}
