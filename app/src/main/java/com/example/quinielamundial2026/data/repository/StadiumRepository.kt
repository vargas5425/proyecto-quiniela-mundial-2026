package com.example.quinielamundial2026.data.repository

import com.example.quinielamundial2026.data.api.ApiService
import com.example.quinielamundial2026.data.database.AppDatabase
import com.example.quinielamundial2026.data.database.entities.StadiumEntity
import com.example.quinielamundial2026.data.models.response.MatchResponse
import com.example.quinielamundial2026.data.models.response.StadiumDetailResponse
import com.example.quinielamundial2026.data.models.response.StadiumResponse

class StadiumRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {

    suspend fun getStadiums(): Result<List<StadiumResponse>> {
        return try {
            val response = apiService.getStadiums()
            if (response.isSuccessful && response.body() != null) {
                val stadiums = response.body()!!
                database.stadiumDao().insertStadiums(
                    stadiums.map {
                        StadiumEntity(
                            id = it.id,
                            name = it.name,
                            city = it.city,
                            country = it.country,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            capacity = it.capacity
                        )
                    }
                )
                Result.success(stadiums)
            } else {
                getStadiumsFromLocal()
            }
        } catch (e: Exception) {
            getStadiumsFromLocal()
        }
    }

    private suspend fun getStadiumsFromLocal(): Result<List<StadiumResponse>> {
        val localStadiums = database.stadiumDao().getAllStadiums()
        return if (localStadiums.isNotEmpty()) {
            Result.success(
                localStadiums.map {
                    StadiumResponse(
                        id = it.id,
                        name = it.name,
                        city = it.city,
                        country = it.country,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        capacity = it.capacity
                    )
                }
            )
        } else {
            Result.failure(Exception("No hay estadios guardados localmente"))
        }
    }

    suspend fun getStadiumDetail(stadiumId: Int): Result<StadiumDetailResponse> {
        return try {
            val response = apiService.getStadiumDetail(stadiumId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val local = database.stadiumDao().getStadiumById(stadiumId)
                if (local != null) {
                    Result.success(
                        StadiumDetailResponse(
                            id = local.id,
                            name = local.name,
                            city = local.city,
                            country = local.country,
                            latitude = local.latitude,
                            longitude = local.longitude,
                            capacity = local.capacity
                        )
                    )
                } else {
                    Result.failure(Exception("No se pudo cargar el detalle del estadio"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getStadiumMatches(stadiumId: Int): Result<List<MatchResponse>> {
        return try {
            val response = apiService.getStadiumMatches(stadiumId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudieron cargar los partidos del estadio"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}