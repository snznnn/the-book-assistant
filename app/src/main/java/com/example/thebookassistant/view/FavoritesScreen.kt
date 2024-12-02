package com.example.thebookassistant.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.thebookassistant.data.DatabaseProvider
import com.example.thebookassistant.data.FavoritedBooks
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavHostController) {
    val db = DatabaseProvider.getDatabase(navController.context)
    val favoriteBooksFlow: Flow<List<FavoritedBooks>> = db.favoritedBooksDao().getAllFavoriteBooks()
    val favoriteBooks by favoriteBooksFlow.collectAsState(initial = emptyList())

    Scaffold(topBar = { TopAppBar(title = { Text("My Favorites List") }) }) { padding ->
// @formatter:off
        Column(modifier = Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
// @formatter:on
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { /* TODO ChatGPT */ }) { Text("Get Suggestions!") }
                Button(onClick = { navController.navigate("CatalogueScreen") }) { Text("Back To Search") }
            }

            if (favoriteBooks.isEmpty()) {
                Text("No favorite books yet!", Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(favoriteBooks) { book ->
                        FavoriteBookItem(book = book)
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteBookItem(book: FavoritedBooks) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Title: ${book.title}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Authors: ${book.authors}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        }
    }
}