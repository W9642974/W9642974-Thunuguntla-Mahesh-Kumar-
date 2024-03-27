package uk.ac.tees.mad.w9642974.presentation.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination

@Composable
fun HomeScreen() {
    Text(text = "Explore screen")
}

object HomeDestination : NavigationDestination {
    override val route = "explore"
}