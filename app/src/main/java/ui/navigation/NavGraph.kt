package com.yourpackage.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import ui.screens.login.LoginScreen
import ui.screens.register.RegisterScreen
import viewmodel.AuthViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                isLoggedIn = authViewModel.isLoggedIn.collectAsState().value,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }

            )
        }

        composable("register") {
            val authViewModel: AuthViewModel = viewModel()
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                onRegisterClick = { email, password ->
                    authViewModel.register(email, password)
                },
                isLoggedIn = authViewModel.isLoggedIn.collectAsState().value,
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
    }
}
