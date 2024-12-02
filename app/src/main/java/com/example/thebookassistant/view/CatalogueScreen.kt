package com.example.thebookassistant.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thebookassistant.api.RetrofitInstance
import com.example.thebookassistant.api.library.model.SearchApiResponse
import com.example.thebookassistant.api.library.model.SearchApiResponseDoc
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
fun CatalogueScreen(navController: NavHostController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Search in OpenLibrary") }) }) { padding ->

        var title by remember { mutableStateOf("") }
        var authorName by remember { mutableStateOf("") }
        var books by remember { mutableStateOf(listOf<SearchApiResponseDoc>()) }

        var isLoading by remember { mutableStateOf(false) }
        val isSearchEnabled = title.isNotEmpty() && authorName.isNotEmpty() && !isLoading

        var errorMessage by remember { mutableStateOf<String?>(null) }
        var noResultsFound by remember { mutableStateOf(false) }
// @formatter:off
        Column(modifier = Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
// @formatter:on
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = authorName, onValueChange = { authorName = it }, label = { Text("Author") }, modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        isLoading = true
                        fetchBooks(
                            title,
                            authorName,
                            { fetchedBooks ->
                                isLoading = false
                                if (fetchedBooks.isEmpty()) {
                                    noResultsFound = true
                                } else {
                                    books = fetchedBooks
                                }
                            },
                            { error ->
                                errorMessage = error
                                isLoading = false
                            }
                        )
                    },
                    enabled = isSearchEnabled
                ) { Text(if (isLoading) "Searching..." else "Search") }
                Button(onClick = { navController.navigate("InventoryScreen") }) { Text("Favorites") }
            }
            Spacer(modifier = Modifier.height(16.dp))

            CatalogueScreenList(books, LocalContext.current)

            if (errorMessage != null) {
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    confirmButton = { Button(onClick = { errorMessage = null }) { Text("OK") } },
                    title = { Text("Something went wrong!") },
                    text = { Text(errorMessage ?: "Unknown error occurred!") }
                )
            }

            if (noResultsFound) {
                AlertDialog(
                    onDismissRequest = { noResultsFound = false },
                    confirmButton = { Button(onClick = { noResultsFound = false }) { Text("OK") } },
                    title = { Text("404") },
                    text = { Text("No results found!") }
                )
            }
        }
    }
}

fun fetchBooks(title: String, author: String, onSuccess: (List<SearchApiResponseDoc>) -> Unit, onError: (String) -> Unit) {
    val searchApiService = RetrofitInstance.openLibrarySearchApiService
    val serviceCall = searchApiService.search(title, author, 5.toString())

    serviceCall.enqueue(object : Callback<SearchApiResponse> {
        override fun onResponse(call: Call<SearchApiResponse>, response: Response<SearchApiResponse>) {
            if (response.isSuccessful) {
                val books = response.body()?.docs ?: emptyList()
                onSuccess(books)
            } else {
                onError("Error: ${response.code()} ${response.message()}")
            }
        }

        override fun onFailure(call: Call<SearchApiResponse>, t: Throwable) {
            onError("Failure: ${t.message}")
        }
    })
}

@Composable
private fun CatalogueScreenList(books: List<SearchApiResponseDoc>, context: Context) {
    val db = DatabaseProvider.getDatabase(context)
    val favoritedBooksDao = db.favoritedBooksDao()

    LazyColumn(modifier = Modifier.fillMaxWidth(0.8f)) {
        items(books) { book ->
// @formatter:off
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Text(text = "Title: ${book.title}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Author(s): ${book.author_name.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                    }
// @formatter:on
                    Button(
                        onClick = {
                            val favoriteBook = FavoritedBooks(title = book.title, authors = book.author_name.joinToString(", "))
                            saveFavoriteBook(favoritedBooksDao, favoriteBook)
                        },
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                    ) { Text("Favorite", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

fun saveFavoriteBook(favoriteBookDao: FavoritedBooksDao, book: FavoritedBooks) {
    CoroutineScope(Dispatchers.IO).launch {
        favoriteBookDao.insertFavoriteBook(book)
    }
}