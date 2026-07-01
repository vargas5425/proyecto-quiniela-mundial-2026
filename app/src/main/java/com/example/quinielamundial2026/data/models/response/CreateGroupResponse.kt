package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class CreateGroupResponse(
    val id: Int,
    val name: String,
    @SerializedName("invite_code")
    val inviteCode: String,
    @SerializedName("created_at")
    val createdAt: String
)