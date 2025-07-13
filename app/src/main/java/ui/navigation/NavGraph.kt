package com.yourpackage.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yourpackage.ui.screens.room.RoomListScreen

import ui.screens.login.LoginScreen
import ui.screens.register.RegisterScreen
import viewmodel.AuthViewModel
import viewmodel.RoomViewModel

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
        composable("home") {
            val roomViewModel: RoomViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()

            val currentUserId = authViewModel.getCurrentUserId()

            if (currentUserId != null) {
                RoomListScreen(
                    viewModel = roomViewModel,
                    currentUserId = currentUserId,
                    onRoomClick = { roomId ->
                        navController.navigate("room/$roomId")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        }
        composable("room/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            // TODO: Navigate to the room's expense screen
        }


    }
}
