package com.example.quinielamundial2026.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.quinielamundial2026.data.database.entities.MatchEntity
import com.example.quinielamundial2026.data.database.relations.MatchWithStadium

@Dao
interface MatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    // ============ CONSULTAS SIMPLES  ============

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Int): MatchEntity?

    // ============ CONSULTAS CON RELACIONES  ============

    @Transaction
    @Query("SELECT * FROM matches ORDER BY matchDate ASC")
    suspend fun getAllMatchesWithStadium(): List<MatchWithStadium>

    @Transaction
    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchWithStadiumById(matchId: Int): MatchWithStadium?

    @Transaction
    @Query("""
        SELECT * FROM matches 
        WHERE status = 'scheduled' AND matchDate > datetime('now') 
        ORDER BY matchDate ASC 
        LIMIT 10
    """)
    suspend fun getNextMatchesWithStadium(): List<MatchWithStadium>
}