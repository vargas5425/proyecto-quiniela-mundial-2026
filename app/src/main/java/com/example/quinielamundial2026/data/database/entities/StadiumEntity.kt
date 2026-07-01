package com.example.quinielamundial2026.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stadiums")
data class StadiumEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int
)