package com.brokenprotocol.firebaseauthdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brokenprotocol.firebaseauthdemo.ui.theme.FirebaseAuthDemoTheme
import com.brokenprotocol.firebaseauthdemo.ui.explore.ExploreScreen
import com.brokenprotocol.firebaseauthdemo.ui.profile.ProfileScreen
import com.brokenprotocol.firebaseauthdemo.ui.profile.EditProfileScreen
import com.brokenprotocol.firebaseauthdemo.ui.auth.AuthScreen
import com.brokenprotocol.firebaseauthdemo.ui.auth.SignInScreen
import com.brokenprotocol.firebaseauthdemo.ui.auth.SignUpScreen
import com.brokenprotocol.firebaseauthdemo.ui.auth.ForgotPasswordScreen
import com.brokenprotocol.firebaseauthdemo.ui.auth.EmailVerificationScreen
import com.brokenprotocol.firebaseauthdemo.ui.auth.AuthViewModel
import com.brokenprotocol.firebaseauthdemo.navigation.NavRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseAuthDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }
    
    // Create shared AuthViewModel instance using Hilt
    val authViewModel: AuthViewModel = hiltViewModel()
    
    // Initialize session on app start
    LaunchedEffect(Unit) {
        // Session initialization is handled in AuthViewModel.init()
        // This ensures the session is checked when the app starts
    }
    
    val tabs = listOf(
        Triple("Explore", Icons.Default.Home, NavRoutes.Explore.route),
        Triple("Profile", Icons.Default.AccountCircle, NavRoutes.Profile.route),
        Triple("Auth", Icons.Default.Lock, NavRoutes.Auth.route)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, (title, icon, route) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { 
                            selectedTab = index
                            navController.navigate(route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Explore.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.Explore.route) {
                ExploreScreen(modifier = Modifier)
            }
            composable(NavRoutes.Profile.route) {
                ProfileScreen(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = authViewModel
                )
            }
            composable(NavRoutes.Auth.route) {
                AuthScreen(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = authViewModel
                )
            }
            composable(NavRoutes.SignIn.route) {
                SignInScreen(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = authViewModel
                )
            }
            composable(NavRoutes.SignUp.route) {
                SignUpScreen(
                    navController = navController,
                    onNavigateToSignIn = {
                        // Navigate to SignIn screen
                        navController.navigate(NavRoutes.SignIn.route)
                    },
                    viewModel = authViewModel
                )
            }
            composable(NavRoutes.ForgotPassword.route) {
                ForgotPasswordScreen(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = authViewModel
                )
            }
            composable(NavRoutes.EmailVerification.route) {
                EmailVerificationScreen(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = authViewModel
                )
            }
            composable(NavRoutes.EditProfile.route) {
                EditProfileScreen(
                    navController = navController,
                    viewModel = authViewModel
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FirebaseAuthDemoTheme {
        MainScreen()
    }
}