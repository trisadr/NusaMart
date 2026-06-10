package com.example.nusamart.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore by preferencesDataStore(name = "token_prefs")

@Singleton
class TokenPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_ROLE = stringPreferencesKey("user_role")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
    }

    // Dipakai untuk OkHttp Interceptor (ambil data secara langsung/blocking)
    suspend fun getTokenSync(): String? {
        return context.tokenDataStore.data.map { it[KEY_TOKEN] }.firstOrNull()
    }

    fun getToken(): Flow<String?> {
        return context.tokenDataStore.data.map { it[KEY_TOKEN] }
    }

    suspend fun getRole(): String? {
        return context.tokenDataStore.data.map { it[KEY_ROLE] }.firstOrNull()
    }

    suspend fun saveSession(token: String, role: String, userId: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_ROLE] = role.lowercase() // samakan case dengan routing mobile (buyer/seller)
            prefs[KEY_USER_ID] = userId
        }
    }

    suspend fun clearSession() {
        context.tokenDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun isLoggedIn(): Boolean {
        val token = getTokenSync()
        return !token.isNullOrEmpty()
    }
}