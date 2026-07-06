package com.example.quinielamundial2026.data.repository

import com.example.quinielamundial2026.data.api.ApiService
import com.example.quinielamundial2026.data.database.AppDatabase
import com.example.quinielamundial2026.data.database.entities.PredictionEntity
import com.example.quinielamundial2026.data.database.relations.PredictionWithMatch
import com.example.quinielamundial2026.data.models.request.PredictionRequest
import com.example.quinielamundial2026.data.models.response.PredictionDetailResponse
import com.example.quinielamundial2026.data.models.response.PredictionResponse
import com.example.quinielamundial2026.data.models.response.MatchResponse

class PredictionRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {

    suspend fun createPrediction(
        matchId: Int,
        homeScore: Int,
        awayScore: Int
    ): Result<PredictionResponse> {

        val tempId = System.currentTimeMillis().toInt() * -1

        val localPrediction = PredictionEntity(
            id = tempId,
            matchId = matchId,
            homeScore = homeScore,
            awayScore = awayScore,
            pointsEarned = null,
            status = "pending",
            synced = false
        )
        database.predictionDao().insertPredictions(listOf(localPrediction))

        return try {
            val response = apiService.createPrediction(
                request = PredictionRequest(
                    matchId = matchId,
                    homeScore = homeScore,
                    awayScore = awayScore
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val prediction = response.body()!!

                val syncedPrediction = PredictionEntity(
                    id = prediction.prediction.id,
                    matchId = prediction.prediction.matchId,
                    homeScore = prediction.prediction.homeScore,
                    awayScore = prediction.prediction.awayScore,
                    pointsEarned = null,
                    status = prediction.prediction.status,
                    synced = true
                )
                database.predictionDao().insertPredictions(listOf(syncedPrediction))

                database.predictionDao().deletePrediction(tempId)

                Result.success(prediction)
            } else {
                val errorMessage = try {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null && errorBody.contains("message")) {
                        val json = org.json.JSONObject(errorBody)
                        json.getString("message")
                    } else {
                        "No se pudo registrar el pronóstico"
                    }
                } catch (e: Exception) {
                    "No se pudo registrar el pronóstico"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Sin conexión. Pronóstico guardado localmente."))
        }
    }

    suspend fun getMyPredictions(): Result<List<PredictionDetailResponse>> {
        return try {
            val response = apiService.getMyPredictions()
            if (response.isSuccessful && response.body() != null) {
                val predictions = response.body()!!
                database.predictionDao().insertPredictions(
                    predictions.map {
                        PredictionEntity(
                            id = it.id,
                            matchId = it.matchId,
                            homeScore = it.homeScore,
                            awayScore = it.awayScore,
                            pointsEarned = it.pointsEarned,
                            status = it.status,
                            synced = true
                        )
                    }
                )
                Result.success(predictions)
            } else {
                getPredictionsFromLocal()
            }
        } catch (e: Exception) {
            getPredictionsFromLocal()
        }
    }

    // ============ OBTENER PREDICCIONES DESDE LOCAL ============
    private suspend fun getPredictionsFromLocal(): Result<List<PredictionDetailResponse>> {
        val predictionsWithMatch = database.predictionDao().getAllPredictionsWithMatch()
        return if (predictionsWithMatch.isNotEmpty()) {
            Result.success(
                predictionsWithMatch.map { predictionWithMatch ->
                    predictionWithMatch.toResponse()
                }
            )
        } else {
            Result.failure(Exception("No hay pronósticos guardados localmente"))
        }
    }

    // ============ OBTENER PREDICCIÓN POR PARTIDO ============
    suspend fun getPredictionByMatch(matchId: Int): PredictionEntity? {
        return try {
            database.predictionDao().getPredictionByMatch(matchId)
        } catch (e: Exception) {
            null
        }
    }

    // ============ SINCRONIZAR PRONÓSTICOS PENDIENTES ============
    suspend fun syncPendingPredictions(): Result<Int> {
        val pending = database.predictionDao().getPendingPredictions()
        var syncedCount = 0
        val errors = mutableListOf<String>()

        pending.forEach { prediction ->
            try {
                val response = apiService.createPrediction(
                    PredictionRequest(
                        matchId = prediction.matchId,
                        homeScore = prediction.homeScore,
                        awayScore = prediction.awayScore
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    val serverPrediction = response.body()!!

                    val syncedPrediction = PredictionEntity(
                        id = serverPrediction.prediction.id,
                        matchId = prediction.matchId,
                        homeScore = prediction.homeScore,
                        awayScore = prediction.awayScore,
                        pointsEarned = null,
                        status = serverPrediction.prediction.status,
                        synced = true
                    )
                    database.predictionDao().insertPredictions(listOf(syncedPrediction))

                    database.predictionDao().deletePrediction(prediction.id)

                    syncedCount++
                } else {
                    errors.add("Error al sincronizar predicción para partido ${prediction.matchId}")
                }
            } catch (e: Exception) {
                errors.add("Error de conexión: ${e.message}")
            }
        }

        return if (errors.isEmpty()) {
            Result.success(syncedCount)
        } else {
            Result.failure(Exception(errors.joinToString("\n")))
        }
    }

    // ============ PRONÓSTICOS PENDIENTES ============
    suspend fun getPendingCount(): Int {
        return try {
            database.predictionDao().getPendingPredictions().size
        } catch (e: Exception) {
            0
        }
    }
}

// ============ FUNCIÓN DE EXTENSIÓN PARA MAPEO ============
private fun PredictionWithMatch.toResponse(): PredictionDetailResponse {
    return PredictionDetailResponse(
        id = prediction.id,
        matchId = prediction.matchId,
        homeScore = prediction.homeScore,
        awayScore = prediction.awayScore,
        pointsEarned = prediction.pointsEarned,
        status = prediction.status,
        match = match?.let { matchEntity ->
            MatchResponse(
                id = matchEntity.id,
                homeTeam = matchEntity.homeTeam,
                awayTeam = matchEntity.awayTeam,
                matchDate = matchEntity.matchDate,
                phase = matchEntity.phase,
                groupName = matchEntity.groupName,
                status = matchEntity.status,
                homeScore = matchEntity.homeScore,
                awayScore = matchEntity.awayScore,
                stadium = null
            )
        }
    )
}