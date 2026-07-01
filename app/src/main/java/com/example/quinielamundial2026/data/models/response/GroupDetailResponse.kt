package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class GroupDetailResponse(
    val id: Int,
    val name: String,
    @SerializedName("invite_code")
    val inviteCode: String,
    val participants: List<Participant>,
    @SerializedName("next_games")
    val nextGames: List<MatchResponse>
) {
    data class Participant(
        val id: Int,
        val name: String,
        val score: Int
    )
}