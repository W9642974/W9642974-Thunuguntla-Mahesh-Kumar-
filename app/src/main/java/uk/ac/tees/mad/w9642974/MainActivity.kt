package uk.ac.tees.mad.w9642974

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.w9642974.presentation.SplashDestination
import uk.ac.tees.mad.w9642974.presentation.SplashScreen
import uk.ac.tees.mad.w9642974.presentation.home.HomeDestination
import uk.ac.tees.mad.w9642974.presentation.home.HomeScreen
import uk.ac.tees.mad.w9642974.ui.theme.GroupFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupFlowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = SplashDestination.route) {
                        composable(SplashDestination.route) {
                            SplashScreen(navHostController = navController)
                        }
                        composable(HomeDestination.route) {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}