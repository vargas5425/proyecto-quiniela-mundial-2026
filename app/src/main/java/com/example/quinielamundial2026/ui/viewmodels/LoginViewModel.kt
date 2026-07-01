package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.AuthRepository
import com.example.quinielamundial2026.ui.states.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Por favor completa todos los campos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = authRepository.login(state.email, state.password)

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSuccess = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}