package com.example.quinielamundial2026.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quinielamundial2026.data.database.entities.PredictionEntity

@Dao
interface PredictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPredictions(predictions: List<PredictionEntity>)

    @Query("SELECT * FROM predictions")
    suspend fun getAllPredictions(): List<PredictionEntity>

    @Query("SELECT * FROM predictions WHERE matchId = :matchId")
    suspend fun getPredictionByMatch(matchId: Int): PredictionEntity?

}