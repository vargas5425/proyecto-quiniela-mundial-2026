package com.example.quinielamundial2026.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quinielamundial2026.data.database.entities.GroupEntity

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Query("SELECT * FROM `groups`")
    suspend fun getAllGroups(): List<GroupEntity>

}