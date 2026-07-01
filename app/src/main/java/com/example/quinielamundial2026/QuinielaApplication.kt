package com.example.quinielamundial2026

import android.app.Application
import com.example.quinielamundial2026.data.database.AppDatabase
import com.example.quinielamundial2026.data.datastore.PreferencesManager
import com.example.quinielamundial2026.di.AppContainer

class QuinielaApplication : Application() {

    companion object {
        lateinit var instance: QuinielaApplication
            private set
    }

    lateinit var container: AppContainer
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        preferencesManager = PreferencesManager(this)
        database = AppDatabase.getInstance(this)

        container = AppContainer(preferencesManager, database)
    }
}