package com.example.quinielamundial2026.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quinielamundial2026.data.database.entities.StadiumEntity

@Dao
interface StadiumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStadiums(stadiums: List<StadiumEntity>)

    @Query("SELECT * FROM stadiums")
    suspend fun getAllStadiums(): List<StadiumEntity>

    @Query("SELECT * FROM stadiums WHERE id = :stadiumId")
    suspend fun getStadiumById(stadiumId: Int): StadiumEntity?

}