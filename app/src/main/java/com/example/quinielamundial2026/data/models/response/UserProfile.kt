package com.example.quinielamundial2026.data.models.response

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val name: String,
    val email: String,
    @SerializedName("total_score")
    val totalScore: Int,
    @SerializedName("groups_count")
    val groupsCount: Int,
    @SerializedName("predictions_count")
    val predictionsCount: Int
)