package com.example.thebookassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thebookassistant.view.CatalogueScreen
import com.example.thebookassistant.view.FavoritesScreen
import com.example.thebookassistant.ui.theme.TheBookAssistantTheme

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
    NavHost(navController = navController, startDestination = "CatalogueScreen") {
        composable("CatalogueScreen") { CatalogueScreen(navController) }
        composable("InventoryScreen") { FavoritesScreen(navController) }
    }
}