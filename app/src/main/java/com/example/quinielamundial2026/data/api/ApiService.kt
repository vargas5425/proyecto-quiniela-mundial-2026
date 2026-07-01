package com.example.quinielamundial2026.data.api

import com.example.quinielamundial2026.data.models.request.*
import com.example.quinielamundial2026.data.models.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============================================================
    //  AUTENTICACION
    // ============================================================
    @POST("api/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/logout")
    suspend fun logout(): Response<Unit>

    // ============================================================
    //  PERFIL
    // ============================================================

    @GET("api/profile")
    suspend fun getProfile(): Response<UserProfile>

    // ============================================================
    //  GRUPOS
    // ============================================================

    @GET("api/groups")
    suspend fun getGroups(): Response<List<GroupResponse>>

    @POST("api/groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest
    ): Response<CreateGroupResponse>

    @POST("api/groups/join")
    suspend fun joinGroup(
        @Body request: JoinGroupRequest
    ): Response<JoinGroupResponse>

    @GET("api/groups/{id}")
    suspend fun getGroupDetail(
        @Path("id") groupId: Int
    ): Response<GroupDetailResponse>

    @GET("api/groups/{id}/leaderboard")
    suspend fun getLeaderboard(
        @Path("id") groupId: Int
    ): Response<List<LeaderboardEntry>>

    // ============================================================
    //  PARTIDOS
    // ============================================================

    @GET("api/matches")
    suspend fun getMatches(
        @Query("next") next: Boolean? = null,
        @Query("phase") phase: String? = null,
        @Query("status") status: String? = null,
        @Query("date") date: String? = null
    ): Response<List<MatchResponse>>

    @GET("api/matches/{id}")
    suspend fun getMatchDetail(
        @Path("id") matchId: Int
    ): Response<MatchDetailResponse>

    @GET("api/matches/updates")
    suspend fun getMatchUpdates(
        @Query("since") since: String? = null
    ): Response<MatchUpdatesResponse>

    // ============================================================
    //  PREDICCIONES
    // ============================================================

    @POST("api/predictions")
    suspend fun createPrediction(
        @Body request: PredictionRequest
    ): Response<PredictionResponse>

    @GET("api/predictions/me")
    suspend fun getMyPredictions(): Response<List<PredictionDetailResponse>>

    // ============================================================
    //  ESTADIOS
    // ============================================================

    @GET("api/stadiums")
    suspend fun getStadiums(): Response<List<StadiumResponse>>

    @GET("api/stadiums/{id}")
    suspend fun getStadiumDetail(
        @Path("id") stadiumId: Int
    ): Response<StadiumDetailResponse>

    @GET("api/stadiums/{id}/matches")
    suspend fun getStadiumMatches(
        @Path("id") stadiumId: Int
    ): Response<List<MatchResponse>>
}