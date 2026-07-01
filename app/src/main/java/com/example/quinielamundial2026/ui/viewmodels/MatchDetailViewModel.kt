package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.data.models.response.PredictionDetailResponse
import com.example.quinielamundial2026.data.repository.MatchRepository
import com.example.quinielamundial2026.data.repository.PredictionRepository
import com.example.quinielamundial2026.ui.states.MatchDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MatchDetailViewModel(
    private val matchRepository: MatchRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchDetailUiState())
    val uiState: StateFlow<MatchDetailUiState> = _uiState

    private var currentMatchId: Int = 0

    fun loadMatchDetail(matchId: Int) {
        currentMatchId = matchId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = matchRepository.getMatchDetail(matchId)
            result.onSuccess { match ->
                _uiState.value = _uiState.value.copy(
                    matchDetail = match,
                    isLoading = false
                )

                try {
                    val database = QuinielaApplication.instance.database
                    val existingPrediction = database.predictionDao().getPredictionByMatch(matchId)

                    if (existingPrediction != null) {
                        val predictionDetail = PredictionDetailResponse(
                            id = existingPrediction.id,
                            matchId = existingPrediction.matchId,
                            homeScore = existingPrediction.homeScore,
                            awayScore = existingPrediction.awayScore,
                            pointsEarned = existingPrediction.pointsEarned,
                            status = existingPrediction.status,
                            match = null
                        )

                        _uiState.value = _uiState.value.copy(
                            homeScore = existingPrediction.homeScore.toString(),
                            awayScore = existingPrediction.awayScore.toString(),
                            myPrediction = predictionDetail
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo cargar el pronóstico guardado"
                    )
                }

            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar partido"
                )
            }
        }
    }

    fun updateHomeScore(score: String) {
        _uiState.value = _uiState.value.copy(homeScore = score)
    }

    fun updateAwayScore(score: String) {
        _uiState.value = _uiState.value.copy(awayScore = score)
    }

    fun submitPrediction() {
        val home = _uiState.value.homeScore.toIntOrNull()
        val away = _uiState.value.awayScore.toIntOrNull()

        if (home == null || away == null || home < 0 || away < 0) {
            _uiState.value = _uiState.value.copy(
                error = "Ingresa marcadores válidos (números positivos)"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = predictionRepository.createPrediction(currentMatchId, home, away)
            result.onSuccess { prediction ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isPredictionSuccess = true,
                    predictionMessage = prediction.message,
                    homeScore = home.toString(),
                    awayScore = away.toString()
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al guardar pronóstico"
                )
            }
        }
    }

    fun resetPredictionState() {
        _uiState.value = _uiState.value.copy(
            isPredictionSuccess = false,
            predictionMessage = null
        )
    }
}