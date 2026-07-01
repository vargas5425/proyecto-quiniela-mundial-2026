package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.GroupRepository
import com.example.quinielamundial2026.ui.states.GroupsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = groupRepository.getGroups()
            result.onSuccess { groups ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    groups = groups
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar grupos"
                )
            }
        }
    }

    fun showCreateDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateDialog = show)
    }

    fun showJoinDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showJoinDialog = show)
    }

    fun updateNewGroupName(name: String) {
        _uiState.value = _uiState.value.copy(newGroupName = name)
    }

    fun updateInviteCode(code: String) {
        _uiState.value = _uiState.value.copy(inviteCode = code)
    }

    fun createGroup() {
        val name = _uiState.value.newGroupName
        if (name.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = groupRepository.createGroup(name)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showCreateDialog = false,
                    newGroupName = ""
                )
                loadGroups()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al crear grupo"
                )
            }
        }
    }

    fun joinGroup() {
        val code = _uiState.value.inviteCode
        if (code.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = groupRepository.joinGroup(code)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showJoinDialog = false,
                    inviteCode = ""
                )
                loadGroups()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al unirse al grupo"
                )
            }
        }
    }
}