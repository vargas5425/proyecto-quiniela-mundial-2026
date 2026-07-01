package com.example.quinielamundial2026.ui.states

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val totalScore: Int = 0,
    val groupsCount: Int = 0,
    val predictionsCount: Int = 0,
    val error: String? = null,
    val isLoggingOut: Boolean = false
)