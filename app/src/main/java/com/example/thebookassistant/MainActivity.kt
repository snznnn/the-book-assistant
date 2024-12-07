package com.example.thebookassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thebookassistant.api.RetrofitInstance
import com.example.thebookassistant.data.DatabaseProvider
import com.example.thebookassistant.view.CatalogueView
import com.example.thebookassistant.ui.theme.TheBookAssistantTheme
import com.example.thebookassistant.view.FavoritesView
import com.example.thebookassistant.view.model.CatalogueViewModel
import com.example.thebookassistant.view.model.FavoritesViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheBookAssistantTheme {
                TheBookAssistant()
            }
        }
    }

}

@Composable
fun TheBookAssistant() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val catalogueViewModel: CatalogueViewModel = viewModel {
        CatalogueViewModel(
            DatabaseProvider.getDatabase(context).favoritedBooksDao(),
            RetrofitInstance.provideOpenLibrarySearchApiService(context)
        )
    }
    val favoritesViewModel: FavoritesViewModel = viewModel {
        FavoritesViewModel(
            DatabaseProvider.getDatabase(context).favoritedBooksDao(),
            RetrofitInstance.provideChatGptCompletionsApiService(context)
        )
    }

    NavHost(navController = navController, startDestination = "CatalogueView") {
        composable("CatalogueView") { CatalogueView(navController, catalogueViewModel) }
        composable("FavoritesView") { FavoritesView(navController, favoritesViewModel) }
    }
}