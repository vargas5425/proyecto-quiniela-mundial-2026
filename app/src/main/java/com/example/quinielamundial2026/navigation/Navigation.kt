package com.example.quinielamundial2026.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.data.api.ApiClient
import com.example.quinielamundial2026.data.repository.AuthRepository
import com.example.quinielamundial2026.ui.screens.GroupDetailScreen
import com.example.quinielamundial2026.ui.screens.GroupsScreen
import com.example.quinielamundial2026.ui.screens.HomeScreen
import com.example.quinielamundial2026.ui.screens.LoginScreen
import com.example.quinielamundial2026.ui.screens.MatchDetailScreen
import com.example.quinielamundial2026.ui.screens.MatchesScreen
import com.example.quinielamundial2026.ui.screens.ProfileScreen
import com.example.quinielamundial2026.ui.screens.RegisterScreen
import com.example.quinielamundial2026.ui.screens.StadiumDetailScreen
import com.example.quinielamundial2026.ui.screens.StadiumMapScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val container = QuinielaApplication.instance.container
    val isLoggedIn = container.authRepository.isLoggedIn()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        // ============ LOGIN ============
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // ============ REGISTER ============
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // ============ PANTALLA PRINCIPAL (HOME) ============
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToGroups = {
                    navController.navigate(Screen.Groups.route)
                },
                onNavigateToMatches = {
                    navController.navigate(Screen.Matches.route)
                },
                onNavigateToStadiums = {
                    navController.navigate(Screen.Stadiums.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ============ PANTALLA DE GRUPOS ============
        composable(Screen.Groups.route) {
            GroupsScreen(
                onNavigateToGroupDetail = { groupId ->
                    navController.navigate(Screen.GroupDetail.passGroupId(groupId))
                }
            )
        }

        // ============ PANTALLA DE DETALLE DE GRUPO ============
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: 0
            GroupDetailScreen(
                navController = navController,
                groupId = groupId
            )
        }

        // ============ PANTALLA DE PARTIDOS ============
        composable(Screen.Matches.route) {
            MatchesScreen(
                onNavigateToMatchDetail = { matchId ->
                    navController.navigate(Screen.MatchDetail.passMatchId(matchId))
                }
            )
        }

        // ============ PANTALLA DE DETALLE DE PARTIDO ============
        composable(
            route = Screen.MatchDetail.route,
            arguments = listOf(navArgument("matchId") { type = NavType.IntType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: 0
            MatchDetailScreen(
                navController = navController,
                matchId = matchId
            )
        }

        // ============ PANTALLA DE ESTADIOS (MAPA) ============
        composable(Screen.Stadiums.route) {
            StadiumMapScreen(
                navController = navController
            )
        }

        // ============ PANTALLA DE PERFIL ============
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        // ============ PANTALLA DE DETALLE DE ESTADIO ============
        composable(
            route = Screen.StadiumDetail.route,
            arguments = listOf(navArgument("stadiumId") { type = NavType.IntType })
        ) { backStackEntry ->
            val stadiumId = backStackEntry.arguments?.getInt("stadiumId") ?: 0
            StadiumDetailScreen(
                navController = navController,
                stadiumId = stadiumId
            )
        }
    }
}