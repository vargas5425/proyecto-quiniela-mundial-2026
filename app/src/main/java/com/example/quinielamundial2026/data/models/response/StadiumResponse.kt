package com.example.quinielamundial2026.data.models.response

data class StadiumResponse(
    val id: Int,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int
)