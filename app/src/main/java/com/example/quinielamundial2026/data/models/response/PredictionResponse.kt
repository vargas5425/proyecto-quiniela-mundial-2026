package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    val message: String,
    val prediction: PredictionInfo
) {
    data class PredictionInfo(
        val id: Int,
        @SerializedName("match_id")
        val matchId: Int,
        @SerializedName("home_score")
        val homeScore: Int,
        @SerializedName("away_score")
        val awayScore: Int,
        val status: String
    )
}