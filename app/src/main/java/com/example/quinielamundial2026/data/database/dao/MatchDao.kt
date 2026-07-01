package com.example.quinielamundial2026.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quinielamundial2026.data.database.entities.MatchEntity

@Dao
interface MatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Query("SELECT * FROM matches ORDER BY matchDate ASC")
    suspend fun getAllMatches(): List<MatchEntity>

    @Query("""
        SELECT * FROM matches 
        WHERE status = 'scheduled' AND matchDate > datetime('now') 
        ORDER BY matchDate ASC 
        LIMIT 10
    """)
    suspend fun getNextMatches(): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Int): MatchEntity?

}