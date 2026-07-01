package com.example.quinielamundial2026.ui.states

import com.example.quinielamundial2026.data.models.response.MatchResponse
import com.example.quinielamundial2026.data.models.response.StadiumDetailResponse

data class StadiumDetailUiState(
    val isLoading: Boolean = false,
    val stadium: StadiumDetailResponse? = null,
    val matches: List<MatchResponse> = emptyList(),
    val error: String? = null
)