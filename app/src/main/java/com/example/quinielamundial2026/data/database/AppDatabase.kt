package com.example.quinielamundial2026.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quinielamundial2026.data.database.dao.*
import com.example.quinielamundial2026.data.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        MatchEntity::class,
        PredictionEntity::class,
        StadiumEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun matchDao(): MatchDao
    abstract fun predictionDao(): PredictionDao
    abstract fun stadiumDao(): StadiumDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiniela_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}