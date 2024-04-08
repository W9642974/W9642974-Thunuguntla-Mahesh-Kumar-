package uk.ac.tees.mad.w9642974

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9642974.presentation.SplashDestination
import uk.ac.tees.mad.w9642974.presentation.SplashScreen
import uk.ac.tees.mad.w9642974.presentation.auth.AuthActionDestination
import uk.ac.tees.mad.w9642974.presentation.auth.AuthActionScreen
import uk.ac.tees.mad.w9642974.presentation.auth.LoginDestination
import uk.ac.tees.mad.w9642974.presentation.auth.LoginScreen
import uk.ac.tees.mad.w9642974.presentation.auth.SignUpScreen
import uk.ac.tees.mad.w9642974.presentation.auth.SignupDestination
import uk.ac.tees.mad.w9642974.presentation.home.AddProjectDestination
import uk.ac.tees.mad.w9642974.presentation.home.AddProjectScreen
import uk.ac.tees.mad.w9642974.presentation.home.HomeDestination
import uk.ac.tees.mad.w9642974.presentation.home.HomeScreen
import uk.ac.tees.mad.w9642974.presentation.home.viewmodels.AddProjectViewModel
import uk.ac.tees.mad.w9642974.presentation.profile.ProfileDestination
import uk.ac.tees.mad.w9642974.presentation.profile.ProfileScreen
import uk.ac.tees.mad.w9642974.ui.theme.GroupFlowTheme

@AndroidEntryPoint
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
                    val scope = rememberCoroutineScope()
                    val firebase = FirebaseAuth.getInstance()
                    val currentUser = firebase.currentUser

                    val initialRoute =
                        if (currentUser != null) {
                            HomeDestination.route
                        } else {
                            AuthActionDestination.route
                        }
                    NavHost(
                        navController = navController,
                        startDestination = SplashDestination.route
                    ) {
                        composable(SplashDestination.route) {
                            SplashScreen(
                                onSplashFinish = {
                                    scope.launch(Dispatchers.Main) {
                                        navController.popBackStack()
                                        navController.navigate(initialRoute)
                                    }
                                }
                            )
                        }
                        composable(HomeDestination.route) {
                            HomeScreen(
                                onProfileClick = {
                                    navController.navigate(ProfileDestination.route)
                                },
                                onAddProject = {
                                    navController.navigate(AddProjectDestination.route)
                                }
                            )
                        }

                        composable(ProfileDestination.route) {
                            ProfileScreen(onLogout = {
                                scope.launch {
                                    firebase.signOut()
                                    navController.navigate(AuthActionDestination.route)
                                }
                            })
                        }

                        composable(AddProjectDestination.route) {
                            val viewModel: AddProjectViewModel = hiltViewModel()
                            val memberList = viewModel.allMembers
                            val createProjectStatus by viewModel.createProjectState.collectAsState(
                                initial = null
                            )
                            AddProjectScreen(
                                onProjectAdded = {
                                    viewModel.addProject(it)
                                },
                                memberList = memberList,
                                createProjectStatus = createProjectStatus,
                                onNavigateUp = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable(AuthActionDestination.route) {
                            AuthActionScreen(
                                onLoginClick = {
                                    navController.navigate(
                                        LoginDestination.route
                                    )
                                },
                                onSignUpClick = {
                                    navController.navigate(
                                        SignupDestination.route
                                    )
                                }
                            )
                        }

                        composable(LoginDestination.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    Toast.makeText(
                                        applicationContext,
                                        "Logged in successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(HomeDestination.route)
                                },
                                onSignUpClick = {
                                    navController.navigate(
                                        SignupDestination.route
                                    )
                                },
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                        composable(SignupDestination.route) {
                            SignUpScreen(
                                onLoginClick = {
                                    navController.navigate(
                                        LoginDestination.route
                                    )
                                },
                                onSignUpSuccess = {
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed up successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(HomeDestination.route)
                                },
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}