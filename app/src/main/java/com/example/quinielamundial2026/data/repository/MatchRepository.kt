package com.example.quinielamundial2026.data.repository

import com.example.quinielamundial2026.data.api.ApiService
import com.example.quinielamundial2026.data.database.AppDatabase
import com.example.quinielamundial2026.data.database.entities.MatchEntity
import com.example.quinielamundial2026.data.models.response.MatchDetailResponse
import com.example.quinielamundial2026.data.models.response.MatchResponse
import com.example.quinielamundial2026.data.models.response.MatchUpdatesResponse
import com.example.quinielamundial2026.utils.NetworkUtils
import com.example.quinielamundial2026.QuinielaApplication

class MatchRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {

    suspend fun getMatches(
        next: Boolean? = null,
        phase: String? = null,
        status: String? = null,
        date: String? = null
    ): Result<List<MatchResponse>> {
        if (next == true) {
            return getMatchesWithOfflineSupport(next = true)
        }

        return getMatchesFromApi(phase, status, date)
    }

    // ============ METODO PRINCIPAL CON SOPORTE OFFLINE ============
    private suspend fun getMatchesWithOfflineSupport(
        next: Boolean? = null
    ): Result<List<MatchResponse>> {
        val context = QuinielaApplication.instance

        if (NetworkUtils.isNetworkAvailable(context)) {
            return try {
                val response = apiService.getMatches(next = true)
                if (response.isSuccessful && response.body() != null) {
                    val matches = response.body()!!
                    saveMatchesToLocal(matches)
                    Result.success(matches)
                } else {
                    getNextMatchesFromLocal()
                }
            } catch (e: Exception) {
                getNextMatchesFromLocal()
            }
        } else {
            return getNextMatchesFromLocal()
        }
    }

    // ============ OBTENER DE LA API ============
    private suspend fun getMatchesFromApi(
        phase: String? = null,
        status: String? = null,
        date: String? = null
    ): Result<List<MatchResponse>> {
        return try {
            val response = apiService.getMatches(null, phase, status, date)
            if (response.isSuccessful && response.body() != null) {
                val matches = response.body()!!
                saveMatchesToLocal(matches)
                Result.success(matches)
            } else {
                getMatchesFromLocal()
            }
        } catch (e: Exception) {
            getMatchesFromLocal()
        }
    }

    // ============ GUARDAR EN BD ============
    private suspend fun saveMatchesToLocal(matches: List<MatchResponse>) {
        database.matchDao().insertMatches(
            matches.map { match ->
                MatchEntity(
                    id = match.id,
                    homeTeam = match.homeTeam,
                    awayTeam = match.awayTeam,
                    matchDate = match.matchDate,
                    phase = match.phase,
                    groupName = match.groupName,
                    status = match.status,
                    homeScore = match.homeScore,
                    awayScore = match.awayScore,
                    stadiumId = match.stadium?.id,
                    stadiumName = match.stadium?.name,
                    stadiumCity = match.stadium?.city,
                    stadiumCountry = match.stadium?.country
                )
            }
        )
    }

    // ============ OBTENER TODOS LOS PARTIDOS DE LA BD (OFFLINE) ============
    private suspend fun getMatchesFromLocal(): Result<List<MatchResponse>> {
        val localMatches = database.matchDao().getAllMatches()
        return if (localMatches.isNotEmpty()) {
            Result.success(
                localMatches.map { entity ->
                    MatchResponse(
                        id = entity.id,
                        homeTeam = entity.homeTeam,
                        awayTeam = entity.awayTeam,
                        matchDate = entity.matchDate,
                        phase = entity.phase,
                        groupName = entity.groupName,
                        status = entity.status,
                        homeScore = entity.homeScore,
                        awayScore = entity.awayScore,
                        stadium = entity.stadiumId?.let {
                            MatchResponse.StadiumInfo(
                                id = it,
                                name = entity.stadiumName ?: "",
                                city = entity.stadiumCity ?: "",
                                country = entity.stadiumCountry ?: ""
                            )
                        }
                    )
                }
            )
        } else {
            Result.failure(Exception("No hay partidos guardados localmente"))
        }
    }

    // ============ OBTENER PRÓXIMOS PARTIDOS DE LA BD (OFFLINE) ============
    suspend fun getNextMatchesFromLocal(): Result<List<MatchResponse>> {
        val localMatches = database.matchDao().getNextMatches()
        return if (localMatches.isNotEmpty()) {
            Result.success(
                localMatches.map { entity ->
                    MatchResponse(
                        id = entity.id,
                        homeTeam = entity.homeTeam,
                        awayTeam = entity.awayTeam,
                        matchDate = entity.matchDate,
                        phase = entity.phase,
                        groupName = entity.groupName,
                        status = entity.status,
                        homeScore = entity.homeScore,
                        awayScore = entity.awayScore,
                        stadium = entity.stadiumId?.let {
                            MatchResponse.StadiumInfo(
                                id = it,
                                name = entity.stadiumName ?: "",
                                city = entity.stadiumCity ?: "",
                                country = entity.stadiumCountry ?: ""
                            )
                        }
                    )
                }
            )
        } else {
            Result.failure(Exception("No hay próximos partidos guardados"))
        }
    }

    // ============ OBTENER DETALLE DE PARTIDO ============
    suspend fun getMatchDetail(matchId: Int): Result<MatchDetailResponse> {
        return try {
            val response = apiService.getMatchDetail(matchId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo cargar el detalle del partido"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // ============ OBTENER ACTUALIZACIONES INCREMENTALES ============
    suspend fun getMatchUpdates(since: String? = null): Result<MatchUpdatesResponse> {
        return try {
            val response = apiService.getMatchUpdates(since)
            if (response.isSuccessful && response.body() != null) {
                val updates = response.body()!!
                updates.matches.forEach { update ->
                    val existing = database.matchDao().getMatchById(update.id)
                    if (existing != null) {
                        val updatedEntity = existing.copy(
                            status = update.status,
                            homeScore = update.homeScore,
                            awayScore = update.awayScore
                        )
                        database.matchDao().insertMatches(listOf(updatedEntity))
                    }
                }
                Result.success(updates)
            } else {
                Result.failure(Exception("No se pudieron obtener las actualizaciones"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}