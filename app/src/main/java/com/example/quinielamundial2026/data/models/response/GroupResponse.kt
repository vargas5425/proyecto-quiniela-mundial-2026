package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class GroupResponse(
    val id: Int,
    val name: String,
    @SerializedName("participants_count")
    val participantsCount: Int,
    @SerializedName("user_score")
    val userScore: Int,
    @SerializedName("invite_code")
    val inviteCode: String
)