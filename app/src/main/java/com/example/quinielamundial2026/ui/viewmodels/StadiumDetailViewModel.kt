package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.StadiumRepository
import com.example.quinielamundial2026.ui.states.StadiumDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StadiumDetailViewModel(
    private val stadiumRepository: StadiumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StadiumDetailUiState())
    val uiState: StateFlow<StadiumDetailUiState> = _uiState

    fun loadStadiumDetail(stadiumId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val stadiumResult = stadiumRepository.getStadiumDetail(stadiumId)
            stadiumResult.onSuccess { stadium ->
                _uiState.value = _uiState.value.copy(stadium = stadium)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Error al cargar el estadio"
                )
            }

            val matchesResult = stadiumRepository.getStadiumMatches(stadiumId)
            matchesResult.onSuccess { matches ->
                _uiState.value = _uiState.value.copy(
                    matches = matches,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar los partidos"
                )
            }
        }
    }
}