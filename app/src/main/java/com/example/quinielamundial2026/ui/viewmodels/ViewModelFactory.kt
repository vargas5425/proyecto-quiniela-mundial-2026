package com.example.quinielamundial2026.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quinielamundial2026.di.AppContainer

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Auth boolean
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(container.authRepository) as T

            // Home
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(
                    container.authRepository,
                    container.groupRepository,
                    container.matchRepository,
                    container.predictionRepository
                ) as T

            // Groups
            modelClass.isAssignableFrom(GroupsViewModel::class.java) ->
                GroupsViewModel(container.groupRepository) as T

            modelClass.isAssignableFrom(GroupDetailViewModel::class.java) ->
                GroupDetailViewModel(container.groupRepository) as T

            // Matches
            modelClass.isAssignableFrom(MatchesViewModel::class.java) ->
                MatchesViewModel(
                    container.matchRepository,
                    container.predictionRepository
                ) as T

            // MatchDetail
            modelClass.isAssignableFrom(MatchDetailViewModel::class.java) ->
                MatchDetailViewModel(
                    container.matchRepository,
                    container.predictionRepository
                ) as T

            // Stadiums
            modelClass.isAssignableFrom(StadiumMapViewModel::class.java) ->
                StadiumMapViewModel(container.stadiumRepository) as T

            // Profile
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(
                    container.authRepository,
                    container.predictionRepository
                ) as T

            // Detail Stadium
            modelClass.isAssignableFrom(StadiumDetailViewModel::class.java) ->
                StadiumDetailViewModel(container.stadiumRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}