package com.example.quinielamundial2026.ui.states

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = ""
)