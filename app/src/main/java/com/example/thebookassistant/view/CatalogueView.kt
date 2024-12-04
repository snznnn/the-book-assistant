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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thebookassistant.api.openlibrary.model.SearchApiResponseDoc
import com.example.thebookassistant.view.model.CatalogueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogueView(navController: NavHostController, viewModel: CatalogueViewModel) {

    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val noResultsFound by viewModel.noResultsFound.collectAsState()
    val favoriteKeys by viewModel.favoriteBooks.collectAsState()

    val title by viewModel.title.collectAsState()
    val authorName by viewModel.authorName.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Search in OpenLibrary") }) }) { padding ->
        val isSearchEnabled = title.isNotEmpty() && authorName.isNotEmpty() && !isLoading

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = title,
                onValueChange = { viewModel.setTitle(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = authorName,
                onValueChange = { viewModel.setAuthorName(it) },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.fetchBooks(title, authorName) }, enabled = isSearchEnabled
                ) {
                    Text(if (isLoading) "Searching..." else "Search")
                }
                Button(onClick = { navController.navigate("FavoritesView") }) { Text("Favorites") }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth(0.8f)) {
                items(books) { book ->
                    CatalogueItem(book = book,
                        isFavorited = favoriteKeys.contains(book.key),
                        onFavorite = { viewModel.favoriteBook(book) },
                        onUnfavorite = { viewModel.unfavoriteBook(book.key) })
                }
            }

            if (errorMessage != null) {
                AlertDialog(onDismissRequest = { },
                    confirmButton = { Button(onClick = { }) { Text("OK") } },
                    title = { Text("Error") },
                    text = { Text(errorMessage ?: "Unknown error occurred!") })
            }

            if (noResultsFound) {
                Text("No results found!", Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun CatalogueItem(
    book: SearchApiResponseDoc,
    isFavorited: Boolean,
    onFavorite: () -> Unit,
    onUnfavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Title: ${book.title}")
                Text(text = "Author(s): ${book.author_name.joinToString(", ")}")
            }
            Button(
                onClick = { if (isFavorited) onUnfavorite() else onFavorite() },
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(0.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(if (isFavorited) "x" else "+")
            }

        }
    }
}
