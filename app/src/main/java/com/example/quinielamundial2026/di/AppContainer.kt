package com.example.quinielamundial2026.di

import com.example.quinielamundial2026.data.api.ApiClient
import com.example.quinielamundial2026.data.database.AppDatabase
import com.example.quinielamundial2026.data.datastore.PreferencesManager
import com.example.quinielamundial2026.data.repository.*

class AppContainer(
    private val preferencesManager: PreferencesManager,
    private val database: AppDatabase
) {

    // ============ API ============
    val apiService = ApiClient.apiService

    // ============ REPOSITORIOS ============
    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService, preferencesManager)
    }

    val groupRepository: GroupRepository by lazy {
        GroupRepository(apiService, database)
    }

    val matchRepository: MatchRepository by lazy {
        MatchRepository(apiService, database)
    }

    val predictionRepository: PredictionRepository by lazy {
        PredictionRepository(apiService, database)
    }

    val stadiumRepository: StadiumRepository by lazy {
        StadiumRepository(apiService, database)
    }
}