package com.example.quinielamundial2026.data.models.request

import com.google.gson.annotations.SerializedName

data class PredictionRequest(
    @SerializedName("match_id")
    val matchId: Int,
    @SerializedName("home_score")
    val homeScore: Int,
    @SerializedName("away_score")
    val awayScore: Int
)