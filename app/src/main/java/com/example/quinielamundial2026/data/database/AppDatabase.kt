package com.example.quinielamundial2026.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("""
                    ALTER TABLE predictions 
                    ADD COLUMN synced INTEGER NOT NULL DEFAULT 0
                """)
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiniela_database.db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}