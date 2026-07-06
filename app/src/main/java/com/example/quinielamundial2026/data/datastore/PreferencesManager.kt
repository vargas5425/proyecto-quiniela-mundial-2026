package com.example.quinielamundial2026.data.datastore

import android.content.Context
import android.content.Context.MODE_PRIVATE

class PreferencesManager(context: Context) {

    companion object {
        private const val PREF_NAME = "quiniela_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_TOKEN_EXPIRY = "auth_token_expiry"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_LAST_SYNC = "last_sync"
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)

    // ============ TOKEN ============

    fun getToken(): String? {
        if (isTokenExpired()) {
            clearAll()
            return null
        }
        return prefs.getString(KEY_TOKEN, null)
    }

    // ============ EXPIRACIÓN DEL TOKEN ============

    fun saveTokenWithExpiry(token: String, expiresInSeconds: Long = 86400) {
        val expiryTime = System.currentTimeMillis() + (expiresInSeconds * 1000)
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_TOKEN_EXPIRY, expiryTime)
            .apply()
    }

    fun getTokenExpiry(): Long? {
        return prefs.getLong(KEY_TOKEN_EXPIRY, 0).takeIf { it > 0 }
    }

    fun isTokenExpired(): Boolean {
        val expiry = getTokenExpiry()
        return if (expiry != null) {
            System.currentTimeMillis() > expiry
        } else {
            false
        }
    }

    // ============ DATOS DEL USUARIO ============

    fun saveUserInfo(name: String, email: String) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    // ============ ÚLTIMA SINCRONIZACIÓN ============
    fun saveLastSync(date: String) {
        prefs.edit().putString(KEY_LAST_SYNC, date).apply()
    }

    fun getLastSync(): String? = prefs.getString(KEY_LAST_SYNC, null)

    // ============ ESTADO DE AUTENTICACIÓN ============

    fun isLoggedIn(): Boolean {
        val token = getToken()
        return token != null && !isTokenExpired()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}