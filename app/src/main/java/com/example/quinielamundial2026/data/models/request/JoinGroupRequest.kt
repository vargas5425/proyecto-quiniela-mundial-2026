package com.example.quinielamundial2026.data.models.request

import com.google.gson.annotations.SerializedName

data class JoinGroupRequest(
    @SerializedName("invite_code")
    val inviteCode: String
)