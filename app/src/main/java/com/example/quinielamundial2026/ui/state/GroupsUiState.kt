package com.example.quinielamundial2026.ui.states

import com.example.quinielamundial2026.data.models.response.GroupResponse

data class GroupsUiState(
    val isLoading: Boolean = false,
    val groups: List<GroupResponse> = emptyList(),
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val showJoinDialog: Boolean = false,
    val newGroupName: String = "",
    val inviteCode: String = ""
)