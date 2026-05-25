package com.example.nusamart.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences 
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserSession(
    val userId: String,
    val role: String,
    val isLogin: Boolean
)

class UserPreference(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_ROLE_KEY = stringPreferencesKey("user_role")
        val IS_LOGIN_KEY = booleanPreferencesKey("is_login")
    }

    fun getSession(): Flow<UserSession> {
        return dataStore.data.map { preferences ->
            UserSession(
                userId = preferences[USER_ID_KEY] ?: "",
                role = preferences[USER_ROLE_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    // Menyimpan session saat user berhasil login
    suspend fun saveSession(userId: String, role: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_ROLE_KEY] = role
            preferences[IS_LOGIN_KEY] = true
        }
    }

    // Menghapus session saat user logout
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
