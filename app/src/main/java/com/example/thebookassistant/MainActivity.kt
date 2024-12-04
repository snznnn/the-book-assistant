package com.example.thebookassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thebookassistant.view.CatalogueView
import com.example.thebookassistant.ui.theme.TheBookAssistantTheme
import com.example.thebookassistant.view.FavoritesView

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

    NavHost(navController = navController, startDestination = "CatalogueView") {
        composable("CatalogueView") { CatalogueView(navController) }
        composable("FavoritesView") { FavoritesView(navController) }
    }
}