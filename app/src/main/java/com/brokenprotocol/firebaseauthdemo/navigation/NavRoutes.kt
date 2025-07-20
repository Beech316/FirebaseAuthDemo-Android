package com.brokenprotocol.firebaseauthdemo.navigation

sealed class NavRoutes(val route: String) {
    object Explore : NavRoutes("explore")
    object Profile : NavRoutes("profile")
    object Auth : NavRoutes("auth")
    object SignIn : NavRoutes("signin")
    object SignUp : NavRoutes("signup")
} 