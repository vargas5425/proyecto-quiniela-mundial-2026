package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.StadiumRepository
import com.example.quinielamundial2026.ui.states.StadiumMapUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StadiumMapViewModel(
    private val stadiumRepository: StadiumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StadiumMapUiState())
    val uiState: StateFlow<StadiumMapUiState> = _uiState

    init {
        loadStadiums()
    }

    fun loadStadiums() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = stadiumRepository.getStadiums()
            result.onSuccess { stadiums ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    stadiums = stadiums
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar estadios"
                )
            }
        }
    }

    fun selectStadium(stadium: com.example.quinielamundial2026.data.models.response.StadiumResponse) {
        _uiState.value = _uiState.value.copy(selectedStadium = stadium)
    }

    fun clearSelectedStadium() {
        _uiState.value = _uiState.value.copy(selectedStadium = null)
    }
}