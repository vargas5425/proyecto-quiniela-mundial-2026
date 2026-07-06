package com.example.quinielamundial2026.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.quinielamundial2026.data.database.entities.MatchEntity
import com.example.quinielamundial2026.data.database.entities.StadiumEntity

data class MatchWithStadium(
    @Embedded
    val match: MatchEntity,

    @Relation(
        parentColumn = "stadiumId",
        entityColumn = "id"
    )
    val stadium: StadiumEntity? = null
)