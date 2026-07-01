package com.example.quinielamundial2026.ui.states

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = ""
)