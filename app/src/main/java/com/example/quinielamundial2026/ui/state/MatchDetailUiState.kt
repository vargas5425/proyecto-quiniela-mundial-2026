package com.example.quinielamundial2026.ui.states

import com.example.quinielamundial2026.data.models.response.MatchDetailResponse
import com.example.quinielamundial2026.data.models.response.PredictionDetailResponse  // <--- IMPORTAR EL REAL

data class MatchDetailUiState(
    val isLoading: Boolean = false,
    val matchDetail: MatchDetailResponse? = null,
    val myPrediction: PredictionDetailResponse? = null,
    val homeScore: String = "",
    val awayScore: String = "",
    val error: String? = null,
    val isPredictionSuccess: Boolean = false,
    val predictionMessage: String? = null
)