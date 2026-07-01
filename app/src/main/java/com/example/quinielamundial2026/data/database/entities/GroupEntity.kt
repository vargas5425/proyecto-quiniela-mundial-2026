package com.example.quinielamundial2026.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val participantsCount: Int,
    val userScore: Int,
    val inviteCode: String
)