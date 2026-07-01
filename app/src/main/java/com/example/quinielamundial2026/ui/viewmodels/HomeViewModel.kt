package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quinielamundial2026.data.repository.*
import com.example.quinielamundial2026.ui.states.HomeUiState
import com.example.quinielamundial2026.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GroupWithPosition(
    val id: Int,
    val name: String,
    val participantsCount: Int,
    val userScore: Int,
    val position: Int
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val matchRepository: MatchRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {

                val userName = authRepository.getUserName() ?: "Usuario"

                var totalScore = 0
                var groupsCount = 0
                var predictionsCount = 0

                val profileResult = authRepository.getProfile()
                profileResult.onSuccess { profile ->
                    totalScore = profile.totalScore
                    groupsCount = profile.groupsCount
                    predictionsCount = profile.predictionsCount
                }

                val groupsResult = groupRepository.getGroups()
                var groupsWithPosition = emptyList<GroupWithPosition>()
                var realGroupsCount = groupsCount

                groupsResult.onSuccess { groups ->
                    realGroupsCount = groups.size
                    groupsWithPosition = groups.map { group ->
                        val position = groups.sortedByDescending { it.userScore }
                            .indexOfFirst { it.id == group.id } + 1
                        GroupWithPosition(
                            id = group.id,
                            name = group.name,
                            participantsCount = group.participantsCount,
                            userScore = group.userScore,
                            position = position
                        )
                    }
                }

                var nextMatches = emptyList<String>()

                val matchesResult = matchRepository.getMatches(next = true)
                matchesResult.onSuccess { matches ->
                    nextMatches = matches.take(5).map { match ->
                        "${match.homeTeam} vs ${match.awayTeam} (${DateUtils.formatDateShort(match.matchDate)})"
                    }
                }

                val predictionsResult = predictionRepository.getMyPredictions()
                var realPredictionsCount = predictionsCount

                predictionsResult.onSuccess { predictions ->
                    realPredictionsCount = predictions.size
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalScore = totalScore,
                    groupsCount = realGroupsCount,
                    predictionsCount = realPredictionsCount,
                    nextMatches = nextMatches,
                    groupsWithPosition = groupsWithPosition
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar datos"
                )
            }
        }
    }

    fun logout(): Result<Unit> {
        var result = Result.success(Unit)
        viewModelScope.launch {
            result = authRepository.logout()
        }
        return result
    }
}