package com.example.thebookassistant.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thebookassistant.data.entity.FavoritedBooks
import com.example.thebookassistant.ui.theme.Palette1
import com.example.thebookassistant.ui.theme.Palette3
import com.example.thebookassistant.ui.theme.Palette4
import com.example.thebookassistant.ui.theme.Palette5
import com.example.thebookassistant.view.model.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesView(navController: NavHostController, viewModel: FavoritesViewModel) {

    val favoriteBooks by viewModel.favoriteBooks.collectAsState(initial = emptyList())
    var selectedBooks by remember { mutableStateOf(setOf<FavoritedBooks>()) }

    val serviceResponse by viewModel.serviceResponse.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val showDialog by viewModel.showDialog.collectAsState()

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("My Favorite Books") }) }) { padding ->
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
                CustomButton(
                    text = (if (isLoading) "Loading..." else "Get Suggestions!"),
                    onClick = { viewModel.getSuggestions(selectedBooks) },
                    enabled = selectedBooks.isNotEmpty() && !isLoading
                )
                CustomButton(
                    text = "Back To Search",
                    onClick = { navController.navigate("CatalogueView") },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                AlertDialog(onDismissRequest = { viewModel.clearErrorMessage() },
                    confirmButton = {
                        Button(onClick = { viewModel.clearErrorMessage() }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Error") },
                    text = { Text(errorMessage ?: "Unknown error occurred!") })
            }

            if (favoriteBooks.isEmpty()) {
                Text("No favorite books yet!", Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth(0.9f)) {
                    items(favoriteBooks) { book ->
                        FavoriteBookItem(book = book,
                            isSelected = selectedBooks.contains(book),
                            onSelectionChange = { isSelected ->
                                selectedBooks = if (isSelected) {
                                    selectedBooks + book
                                } else {
                                    selectedBooks - book
                                }
                            },
                            onDelete = { viewModel.deleteFavoriteBook(book) })
                    }
                }
            }
        }
    }

    if (showDialog) {
        ResponseDialog(responseText = serviceResponse, onDismiss = { viewModel.closeDialog() })
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
        colors = CardDefaults.cardColors(containerColor = Palette1),
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Title: ${book.title}")
                Text(text = "Author(s): ${book.authors}")
            }
            Checkbox(
                colors = CheckboxDefaults.colors(
                    checkedColor = Palette5,
                    uncheckedColor = Palette4,
                    checkmarkColor = Color.White),
                checked = isSelected,
                onCheckedChange = onSelectionChange,
                modifier = Modifier.size(24.dp).padding(end = 8.dp)
            )
            Button(
                colors = ButtonDefaults.buttonColors(Palette5),
                onClick = onDelete,
                modifier = Modifier.size(22.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(3.dp)
            ) {
                Text(
                    "X",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun ResponseDialog(responseText: String, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Palette3, contentColor = Color.White,),
            onClick = { onDismiss() }) {
            Text("Close")
        }
    }, title = { Text("Suggestions by ChatGPT") }, text = {
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.6f)
            ) {
                item {
                    Text(
                        text = responseText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    })
}
