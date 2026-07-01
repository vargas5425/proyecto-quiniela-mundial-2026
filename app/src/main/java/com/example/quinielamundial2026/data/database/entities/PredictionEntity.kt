package com.example.quinielamundial2026.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey
    val id: Int,
    val matchId: Int,
    val homeScore: Int,
    val awayScore: Int,
    val pointsEarned: Int? = null,
    val status: String
)