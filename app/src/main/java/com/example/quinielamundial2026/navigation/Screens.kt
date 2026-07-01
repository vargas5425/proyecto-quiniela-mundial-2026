package com.example.quinielamundial2026.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Groups : Screen("groups")
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun passGroupId(groupId: Int): String = "group_detail/$groupId"
    }
    object Matches : Screen("matches")
    object MatchDetail : Screen("match_detail/{matchId}") {
        fun passMatchId(matchId: Int): String = "match_detail/$matchId"
    }
    object Stadiums : Screen("stadiums")
    object StadiumDetail : Screen("stadium_detail/{stadiumId}") {
        fun passStadiumId(stadiumId: Int): String = "stadium_detail/$stadiumId"
    }
    object Profile : Screen("profile")
}