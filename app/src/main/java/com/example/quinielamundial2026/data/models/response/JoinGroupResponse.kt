package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class JoinGroupResponse(
    val message: String,
    val group: GroupInfo
) {
    data class GroupInfo(
        val id: Int,
        val name: String,
        @SerializedName("participants_count")
        val participantsCount: Int
    )
}