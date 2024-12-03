package com.example.thebookassistant.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thebookassistant.BuildConfig
import com.example.thebookassistant.api.RetrofitInstance
import com.example.thebookassistant.api.library.ChatGptCompletionsApiService
import com.example.thebookassistant.api.library.model.ChatGptApiRequest
import com.example.thebookassistant.api.library.model.ChatGptApiRequestMessage
import com.example.thebookassistant.api.library.model.ChatGptApiResponse
import com.example.thebookassistant.data.DatabaseProvider
import com.example.thebookassistant.data.FavoritedBooks
import com.example.thebookassistant.data.FavoritedBooksDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavHostController) {
    val favoriteBooksDao = DatabaseProvider.getDatabase(navController.context).favoritedBooksDao()
    val favoriteBooks by favoriteBooksDao.getAllFavoriteBooks()
        .collectAsState(initial = emptyList())

    val selectedBooks = remember { mutableStateOf(mutableSetOf<FavoritedBooks>()) }
    val chatGptService = RetrofitInstance.chatGptCompletionsApiService
    var isLoading by remember { mutableStateOf(false) }
    val serviceResponse = remember { mutableStateOf("No suggestions yet..") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State for showing the dialog
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("My Favorite Books") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (selectedBooks.value.isNotEmpty()) {
                            isLoading = true
                            getSuggestions(selectedBooks.value, chatGptService, { result ->
                                serviceResponse.value = result
                                isLoading = false
                                showDialog.value = true // Show dialog after receiving response
                            }, { error ->
                                errorMessage = error
                                isLoading = false
                            })
                        }
                    },
                    enabled = selectedBooks.value.isNotEmpty() && !isLoading
                ) {
                    Text(if (isLoading) "Loading..." else "Get Suggestions!")
                }
                Button(onClick = { navController.navigate("CatalogueScreen") }) {
                    Text("Back To Search")
                }
            }

            if (errorMessage != null) {
                AlertDialog(onDismissRequest = { errorMessage = null },
                    confirmButton = { Button(onClick = { errorMessage = null }) { Text("OK") } },
                    title = { Text("Something went wrong!") },
                    text = { Text(errorMessage ?: "Unknown error occurred!") })
            }

            if (favoriteBooks.isEmpty()) {
                Text("No favorite books yet!", Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(favoriteBooks) { book ->
                        FavoriteBookItem(book = book,
                            isSelected = selectedBooks.value.contains(book),
                            onSelectionChange = { isSelected ->
                                selectedBooks.value = selectedBooks.value.toMutableSet().apply {
                                    if (isSelected) {
                                        add(book)
                                    } else {
                                        remove(book)
                                    }
                                }
                            },
                            onDelete = {
                                deleteFavoriteBook(favoriteBooksDao, book)
                            })
                    }
                }
            }
        }
    }

    // Show the dialog when API response is ready
    if (showDialog.value) {
        ResponseDialog(serviceResponse.value, onDismiss = { showDialog.value = false })
    }
}

fun getSuggestions(
    selectedBooks: Set<FavoritedBooks>,
    chatGptService: ChatGptCompletionsApiService,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val booksInput = selectedBooks.joinToString(" | ") { "${it.title} by ${it.authors}" }
    val chatGptRequest = ChatGptApiRequest(
        model = "gpt-4o-mini", messages = listOf(
            ChatGptApiRequestMessage(
                role = "user", content = """
        Please make 5 book suggestions including short descriptions based on the following inputs: $booksInput
    """.trimIndent()
            )
        )
    )

    val apiKey = BuildConfig.CHATGPT_API_KEY
    val call = chatGptService.completions(
        "Bearer $apiKey",
        chatGptRequest
    )

    call.enqueue(object : Callback<ChatGptApiResponse> {
        override fun onResponse(
            call: Call<ChatGptApiResponse>, response: Response<ChatGptApiResponse>
        ) {
            if (response.isSuccessful) {
                val reply =
                    response.body()?.choices?.firstOrNull()?.message?.content ?: "No response."
                onSuccess(reply)
            } else {
                onError("Error: ${response.code()} ${response.message()}")
            }
        }

        override fun onFailure(call: Call<ChatGptApiResponse>, t: Throwable) {
            onError("Failure: ${t.message}")
        }
    })

}

fun deleteFavoriteBook(favoriteBookDao: FavoritedBooksDao, book: FavoritedBooks) {
    CoroutineScope(Dispatchers.IO).launch {
        favoriteBookDao.deleteFavoriteBook(book)
    }
}

@Composable
fun FavoriteBookItem(
    book: FavoritedBooks,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Title: ${book.title}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Author(s): ${book.authors}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .size(if (isSelected) 38.dp else 36.dp)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp),
                contentPadding = PaddingValues(0.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    "X",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun ResponseDialog(responseText: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        },
        title = { Text("Suggestions by ChatGPT") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = responseText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    )
}