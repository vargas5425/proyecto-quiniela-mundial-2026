package com.example.quinielamundial2026.data.repository

import com.example.quinielamundial2026.data.api.ApiService
import com.example.quinielamundial2026.data.database.AppDatabase
import com.example.quinielamundial2026.data.database.entities.GroupEntity
import com.example.quinielamundial2026.data.models.request.CreateGroupRequest
import com.example.quinielamundial2026.data.models.request.JoinGroupRequest
import com.example.quinielamundial2026.data.models.response.*

class GroupRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {

    suspend fun getGroups(): Result<List<GroupResponse>> {
        return try {
            val response = apiService.getGroups()
            if (response.isSuccessful && response.body() != null) {
                val groups = response.body()!!
                database.groupDao().insertGroups(
                    groups.map {
                        GroupEntity(
                            id = it.id,
                            name = it.name,
                            participantsCount = it.participantsCount,
                            userScore = it.userScore,
                            inviteCode = it.inviteCode
                        )
                    }
                )
                Result.success(groups)
            } else {
                getGroupsFromLocal()
            }
        } catch (e: Exception) {
            getGroupsFromLocal()
        }
    }

    private suspend fun getGroupsFromLocal(): Result<List<GroupResponse>> {
        val localGroups = database.groupDao().getAllGroups()
        return if (localGroups.isNotEmpty()) {
            Result.success(
                localGroups.map {
                    GroupResponse(
                        id = it.id,
                        name = it.name,
                        participantsCount = it.participantsCount,
                        userScore = it.userScore,
                        inviteCode = it.inviteCode
                    )
                }
            )
        } else {
            Result.failure(Exception("No hay grupos guardados localmente"))
        }
    }

    suspend fun createGroup(name: String): Result<CreateGroupResponse> {
        return try {
            val response = apiService.createGroup(CreateGroupRequest(name))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear el grupo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun joinGroup(inviteCode: String): Result<JoinGroupResponse> {
        return try {
            val response = apiService.joinGroup(JoinGroupRequest(inviteCode))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Código de invitación inválido"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getGroupDetail(groupId: Int): Result<GroupDetailResponse> {
        return try {
            val response = apiService.getGroupDetail(groupId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo cargar el detalle del grupo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getLeaderboard(groupId: Int): Result<List<LeaderboardEntry>> {
        return try {
            val response = apiService.getLeaderboard(groupId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo cargar la clasificación"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}