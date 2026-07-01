package com.example.quinielamundial2026.ui.states

import com.example.quinielamundial2026.ui.viewmodels.GroupWithPosition

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val totalScore: Int = 0,
    val groupsCount: Int = 0,
    val predictionsCount: Int = 0,
    val nextMatches: List<String> = emptyList(),
    val groupsWithPosition: List<GroupWithPosition> = emptyList(),
    val error: String? = null
)