package com.example.quinielamundial2026.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String,
    val email: String,
    val totalScore: Int = 0,
    val groupsCount: Int = 0,
    val predictionsCount: Int = 0
)