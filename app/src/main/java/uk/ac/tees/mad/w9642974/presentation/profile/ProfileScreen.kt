package uk.ac.tees.mad.w9642974.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column {
        Button(onClick = onLogout) {
            Text(text = "Log out")
        }
    }
}

object ProfileDestination : NavigationDestination {
    override val route = "profile"
}