 package com.example.thebookassistant.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thebookassistant.BuildConfig
import com.example.thebookassistant.api.chatgpt.ChatGptCompletionsApiService
import com.example.thebookassistant.api.chatgpt.model.ChatGptApiRequest
import com.example.thebookassistant.api.chatgpt.model.ChatGptApiRequestMessage
import com.example.thebookassistant.api.chatgpt.model.ChatGptApiResponse
import com.example.thebookassistant.data.dao.FavoritedBooksDao
import com.example.thebookassistant.data.entity.FavoritedBooks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 class FavoritesViewModel(
    private val favoritedBooksDao: FavoritedBooksDao,
    private val chatGptService: ChatGptCompletionsApiService
) : ViewModel() {

    val favoriteBooks: Flow<List<FavoritedBooks>> = favoritedBooksDao.getAllFavoriteBooks()

    fun deleteFavoriteBook(book: FavoritedBooks) {
        viewModelScope.launch {
            favoritedBooksDao.deleteFavoriteBook(book)
        }
    }

    val serviceResponse = MutableStateFlow("No suggestions yet...")
    val isLoading = MutableStateFlow(false)
    val showDialog = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun getSuggestions(selectedBooks: Set<FavoritedBooks>) {
        if (selectedBooks.isEmpty()) return

        isLoading.value = true
        val booksInput = selectedBooks.joinToString(" | ") { "${it.title} by ${it.authors}" }
        val chatGptRequest = ChatGptApiRequest(
            model = "gpt-4o-mini",
            messages = listOf(
                ChatGptApiRequestMessage(
                    role = "user",
                    content = "Please make 5 book suggestions including short descriptions based on the following inputs: $booksInput"
                )
            )
        )

        chatGptService.completions("Bearer ${BuildConfig.CHATGPT_API_KEY}", chatGptRequest)
            .enqueue(object : Callback<ChatGptApiResponse> {
                override fun onResponse(
                    call: Call<ChatGptApiResponse>,
                    response: Response<ChatGptApiResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        serviceResponse.value =
                            response.body()?.choices?.firstOrNull()?.message?.content
                                ?: "No response."
                        showDialog.value = true
                    } else {
                        errorMessage.value = "Error: ${response.code()} ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<ChatGptApiResponse>, t: Throwable) {
                    isLoading.value = false
                    errorMessage.value = "Failure: ${t.message}"
                }
            })
    }

    fun closeDialog() {
        showDialog.value = false
    }

    fun clearErrorMessage() {
        errorMessage.value = null
    }

}