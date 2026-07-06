package com.example.quinielamundial2026.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.quinielamundial2026.data.database.entities.PredictionEntity
import com.example.quinielamundial2026.data.database.relations.PredictionWithMatch

@Dao
interface PredictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPredictions(predictions: List<PredictionEntity>)

    // ============ CONSULTAS SIMPLES ============

    @Query("SELECT * FROM predictions WHERE matchId = :matchId")
    suspend fun getPredictionByMatch(matchId: Int): PredictionEntity?

    // ============ CONSULTAS CON RELACIONES ============

    @Transaction
    @Query("SELECT * FROM predictions")
    suspend fun getAllPredictionsWithMatch(): List<PredictionWithMatch>

    // ============ PRONÓSTICOS PENDIENTES ============

    @Query("SELECT * FROM predictions WHERE synced = 0")
    suspend fun getPendingPredictions(): List<PredictionEntity>

    @Query("DELETE FROM predictions WHERE id = :predictionId")
    suspend fun deletePrediction(predictionId: Int)

}