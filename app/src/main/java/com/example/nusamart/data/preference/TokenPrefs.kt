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
        private val KEY_ROLE = stringPreferencesKey("user_role")  // ← tambah
    }

    // ── Token ──────────────────────────────────────────────
    val tokenFlow: Flow<String?> = context.tokenDataStore.data
        .map { prefs -> prefs[KEY_TOKEN] }

    suspend fun getToken(): String? {
        return context.tokenDataStore.data
            .map { prefs -> prefs[KEY_TOKEN] }
            .firstOrNull()
    }

    suspend fun saveToken(token: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
        }
    }

    // ── Role ───────────────────────────────────────────────
    suspend fun getRole(): String? {
        return context.tokenDataStore.data
            .map { prefs -> prefs[KEY_ROLE] }
            .firstOrNull()
    }

    suspend fun saveRole(role: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[KEY_ROLE] = role
        }
    }

    // ── Save keduanya sekaligus setelah login ──────────────
    suspend fun saveSession(token: String, role: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_ROLE] = role
        }
    }

    // ── Clear semua saat logout ────────────────────────────
    suspend fun clearSession() {
        context.tokenDataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_ROLE)
        }
    }

    suspend fun isLoggedIn(): Boolean = getToken() != null
}