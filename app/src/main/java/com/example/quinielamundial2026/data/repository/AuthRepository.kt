package com.example.quinielamundial2026.data.repository

import com.example.quinielamundial2026.QuinielaApplication
import com.example.quinielamundial2026.data.api.ApiService
import com.example.quinielamundial2026.data.database.entities.UserEntity
import com.example.quinielamundial2026.data.datastore.PreferencesManager
import com.example.quinielamundial2026.data.models.request.LoginRequest
import com.example.quinielamundial2026.data.models.request.RegisterRequest
import com.example.quinielamundial2026.data.models.response.AuthResponse
import com.example.quinielamundial2026.data.models.response.UserProfile

class AuthRepository(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                preferencesManager.saveTokenWithExpiry(
                    token = data.token,
                    expiresInSeconds = 86400
                )
                preferencesManager.saveUserInfo(data.name, data.email)
                Result.success(data)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(name, email, password, passwordConfirmation)
            )
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Result.success(data)
            } else {
                Result.failure(Exception("Error al registrar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                preferencesManager.clearAll()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al cerrar sesión"))
            }
        } catch (e: Exception) {
            preferencesManager.clearAll()
            Result.success(Unit)
        }
    }

    // ============ PERFIL ============

    suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val profile = response.body()!!
                saveProfileToLocal(profile)
                Result.success(profile)
            } else {

                if (response.code() == 401) {
                    preferencesManager.clearAll()
                    Result.failure(Exception("Sesión expirada. Inicia sesión nuevamente."))
                } else {
                    val localProfile = getProfileFromLocal()
                    if (localProfile != null) {
                        Result.success(localProfile)
                    } else {
                        Result.failure(Exception("Error al obtener perfil"))
                    }
                }
            }
        } catch (e: Exception) {
            val localProfile = getProfileFromLocal()
            if (localProfile != null) {
                Result.success(localProfile)
            } else {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    // ============ PERFIL LOCAL (OFFLINE) ============

    private suspend fun saveProfileToLocal(profile: UserProfile) {
        val database = QuinielaApplication.instance.database
        database.userDao().insertUser(
            UserEntity(
                id = 1,
                name = profile.name,
                email = profile.email,
                totalScore = profile.totalScore,
                groupsCount = profile.groupsCount,
                predictionsCount = profile.predictionsCount
            )
        )
    }

    private suspend fun getProfileFromLocal(): UserProfile? {
        val database = QuinielaApplication.instance.database
        val user = database.userDao().getUser()
        return user?.let {
            UserProfile(
                name = it.name,
                email = it.email,
                totalScore = it.totalScore,
                groupsCount = it.groupsCount,
                predictionsCount = it.predictionsCount
            )
        }
    }

    // ============ MÉTODOS DE CONSULTA ============

    fun isLoggedIn(): Boolean = preferencesManager.isLoggedIn()
    fun getUserName(): String? = preferencesManager.getUserName()
    fun getUserEmail(): String? = preferencesManager.getUserEmail()

}