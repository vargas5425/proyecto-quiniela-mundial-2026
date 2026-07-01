package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.GroupRepository
import com.example.quinielamundial2026.ui.states.GroupDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState

    private var currentGroupId: Int = 0

    fun loadGroupDetail(groupId: Int) {
        currentGroupId = groupId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val detailResult = groupRepository.getGroupDetail(groupId)
            detailResult.onSuccess { detail ->
                _uiState.value = _uiState.value.copy(
                    groupDetail = detail,
                    matches = detail.nextGames
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Error al cargar detalle del grupo"
                )
            }

            val leaderboardResult = groupRepository.getLeaderboard(groupId)
            leaderboardResult.onSuccess { leaderboard ->
                _uiState.value = _uiState.value.copy(
                    leaderboard = leaderboard,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar clasificación"
                )
            }
        }
    }
}