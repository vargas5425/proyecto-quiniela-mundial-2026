package com.example.quinielamundial2026.ui.states

import com.example.quinielamundial2026.data.models.response.StadiumResponse

data class StadiumMapUiState(
    val isLoading: Boolean = false,
    val stadiums: List<StadiumResponse> = emptyList(),
    val error: String? = null,
    val selectedStadium: StadiumResponse? = null
)