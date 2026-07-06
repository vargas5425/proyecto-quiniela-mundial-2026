package com.example.quinielamundial2026.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.quinielamundial2026.data.database.entities.MatchEntity
import com.example.quinielamundial2026.data.database.entities.PredictionEntity

data class MatchWithPredictions(
    @Embedded
    val match: MatchEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "matchId"
    )
    val predictions: List<PredictionEntity> = emptyList()
)