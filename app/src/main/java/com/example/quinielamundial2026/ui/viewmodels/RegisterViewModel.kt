package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.AuthRepository
import com.example.quinielamundial2026.ui.states.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updatePasswordConfirmation(passwordConfirmation: String) {
        _uiState.value = _uiState.value.copy(passwordConfirmation = passwordConfirmation)
    }

    fun register() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "El nombre es obligatorio")
            return
        }
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.value = _uiState.value.copy(error = "Ingresa un email válido")
            return
        }
        if (state.password.length < 8) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 8 caracteres")
            return
        }
        if (state.password != state.passwordConfirmation) {
            _uiState.value = _uiState.value.copy(error = "Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = authRepository.register(
                state.name,
                state.email,
                state.password,
                state.passwordConfirmation
            )

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSuccess = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Error al registrar usuario"
                )
            }
        }
    }

    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}