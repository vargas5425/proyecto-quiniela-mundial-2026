package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class MatchResponse(
    val id: Int,
    @SerializedName("home_team")
    val homeTeam: String,
    @SerializedName("away_team")
    val awayTeam: String,
    @SerializedName("match_date")
    val matchDate: String,
    val phase: String,
    @SerializedName("group_name")
    val groupName: String? = null,
    val status: String,
    @SerializedName("home_score")
    val homeScore: Int? = null,
    @SerializedName("away_score")
    val awayScore: Int? = null,
    val stadium: StadiumInfo? = null
) {
    data class StadiumInfo(
        val id: Int,
        val name: String,
        val city: String,
        val country: String
    )
}