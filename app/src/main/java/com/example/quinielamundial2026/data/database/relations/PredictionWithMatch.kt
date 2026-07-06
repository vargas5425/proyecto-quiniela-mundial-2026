package com.example.quinielamundial2026.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.quinielamundial2026.data.database.entities.MatchEntity
import com.example.quinielamundial2026.data.database.entities.PredictionEntity

data class PredictionWithMatch(
    @Embedded
    val prediction: PredictionEntity,

    @Relation(
        parentColumn = "matchId",
        entityColumn = "id"
    )
    val match: MatchEntity? = null
)