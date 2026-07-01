package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class PredictionDetailResponse(
    val id: Int,
    @SerializedName("match_id")
    val matchId: Int,
    @SerializedName("home_score")
    val homeScore: Int,
    @SerializedName("away_score")
    val awayScore: Int,
    @SerializedName("points_earned")
    val pointsEarned: Int? = null,
    val status: String,
    val match: MatchResponse? = null
)