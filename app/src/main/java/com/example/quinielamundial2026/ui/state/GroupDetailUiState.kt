package com.example.quinielamundial2026.ui.states

import com.example.quinielamundial2026.data.models.response.GroupDetailResponse
import com.example.quinielamundial2026.data.models.response.LeaderboardEntry
import com.example.quinielamundial2026.data.models.response.MatchResponse

data class GroupDetailUiState(
    val isLoading: Boolean = false,
    val groupDetail: GroupDetailResponse? = null,
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val matches: List<MatchResponse> = emptyList(),
    val error: String? = null
)