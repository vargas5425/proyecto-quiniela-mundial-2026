package com.example.quinielamundial2026.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val matchDate: String,
    val phase: String,
    val groupName: String? = null,
    val status: String,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val stadiumId: Int? = null
)