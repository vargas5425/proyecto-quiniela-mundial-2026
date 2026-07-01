package com.example.quinielamundial2026.ui.states

import com.example.quinielamundial2026.data.models.response.MatchResponse

data class MatchesUiState(
    val isLoading: Boolean = false,
    val matches: List<MatchResponse> = emptyList(),
    val error: String? = null,
    val selectedPhase: String? = null,
    val selectedStatus: String? = null,
    val selectedDate: String? = null
)