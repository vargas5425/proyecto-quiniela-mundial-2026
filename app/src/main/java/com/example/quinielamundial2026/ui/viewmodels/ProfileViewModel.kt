package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.AuthRepository
import com.example.quinielamundial2026.data.repository.PredictionRepository
import com.example.quinielamundial2026.ui.states.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {

                val userName = authRepository.getUserName() ?: "Usuario"
                val userEmail = authRepository.getUserEmail() ?: "email@ejemplo.com"

                var totalScore = 0
                var groupsCount = 0
                var predictionsCount = 0

                val profileResult = authRepository.getProfile()
                profileResult.onSuccess { profile ->
                    totalScore = profile.totalScore
                    groupsCount = profile.groupsCount
                    predictionsCount = profile.predictionsCount
                }

                if (profileResult.isFailure) {
                    val predictionsResult = predictionRepository.getMyPredictions()
                    predictionsResult.onSuccess { predictions ->
                        predictionsCount = predictions.size
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userName = userName,
                    userEmail = userEmail,
                    totalScore = totalScore,
                    groupsCount = groupsCount,
                    predictionsCount = predictionsCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar perfil"
                )
            }
        }
    }

    fun logout(): Result<Unit> {
        var result = Result.success(Unit)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoggingOut = true)
            result = authRepository.logout()
            _uiState.value = _uiState.value.copy(isLoggingOut = false)
        }
        return result
    }
}