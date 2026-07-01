package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class MatchUpdatesResponse(
    @SerializedName("synced_at")
    val syncedAt: String,
    val matches: List<MatchUpdate>
) {
    data class MatchUpdate(
        val id: Int,
        @SerializedName("home_team")
        val homeTeam: String,
        @SerializedName("away_team")
        val awayTeam: String,
        val status: String,
        @SerializedName("home_score")
        val homeScore: Int? = null,
        @SerializedName("away_score")
        val awayScore: Int? = null
    )
}