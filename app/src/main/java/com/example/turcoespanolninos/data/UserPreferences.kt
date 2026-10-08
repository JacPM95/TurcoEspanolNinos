package com.example.turcoespanolninos.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val ALIAS_KEY = stringPreferencesKey("alias_usuario")
    }

    val guardarAlias: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ALIAS_KEY] ?: ""
    }

    suspend fun salvarAlias(alias: String) {
        context.dataStore.edit { preferences ->
            preferences[ALIAS_KEY] = alias
        }
    }
}