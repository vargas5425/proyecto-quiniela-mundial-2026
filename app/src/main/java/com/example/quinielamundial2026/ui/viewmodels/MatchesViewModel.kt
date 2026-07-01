package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.data.repository.MatchRepository
import com.example.quinielamundial2026.ui.states.MatchesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MatchesViewModel(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState

    init {
        loadMatches()
    }

    fun loadMatches(
        phase: String? = null,
        status: String? = null,
        date: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val preferencesManager = QuinielaApplication.instance.preferencesManager
                val lastSync = preferencesManager.getLastSync()

                if (lastSync != null) {
                    val updatesResult = matchRepository.getMatchUpdates(since = lastSync)
                    updatesResult.onSuccess { updates ->
                        preferencesManager.saveLastSync(updates.syncedAt)
                    }
                }

                val result = matchRepository.getMatches(
                    next = null,
                    phase = phase,
                    status = status,
                    date = date
                )

                result.onSuccess { matches ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        matches = matches,
                        selectedPhase = phase,
                        selectedStatus = status,
                        selectedDate = date
                    )

                    if (lastSync == null && matches.isNotEmpty()) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        val currentDate = dateFormat.format(Date())
                        preferencesManager.saveLastSync(currentDate)
                    }

                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar partidos"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar partidos"
                )
            }
        }
    }

    fun loadNextMatches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = matchRepository.getMatches(next = true)
            result.onSuccess { matches ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    matches = matches
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar próximos partidos"
                )
            }
        }
    }

    fun filterByPhase(phase: String) {
        loadMatches(phase = phase)
    }

    fun filterByStatus(status: String) {
        loadMatches(status = status)
    }

    fun clearFilters() {
        loadMatches()
    }
}