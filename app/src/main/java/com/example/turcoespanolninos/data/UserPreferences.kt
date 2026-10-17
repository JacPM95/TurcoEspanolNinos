package com.example.turcoespanolninos.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val ALIAS_KEY = stringPreferencesKey("alias_usuario")
        val EDAD_KEY = intPreferencesKey("edad_usuario")
        val NIVEL_KEY = intPreferencesKey("nivel_desbloqueado")
        val ESTRELLAS_KEY = intPreferencesKey("estrellas_acumuladas")
        val PALABRAS_KEY = stringSetPreferencesKey("palabras_completadas")
    }

    val aliasGuardado: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ALIAS_KEY] ?: ""
    }

    val edadGuardada: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[EDAD_KEY] ?: 0
    }

    val progresoGuardado: Flow<ProgresoGuardado> = context.dataStore.data.map { preferences ->
        ProgresoGuardado(
            nivelDesbloqueado = preferences[NIVEL_KEY] ?: 0,
            estrellas = preferences[ESTRELLAS_KEY] ?: 0,
            palabrasCompletadas = preferences[PALABRAS_KEY] ?: emptySet()
        )
    }

    suspend fun guardarPerfil(alias: String, edad: Int) {
        context.dataStore.edit { preferences ->
            preferences[ALIAS_KEY] = alias
            preferences[EDAD_KEY] = edad
        }
    }

    suspend fun guardarProgreso(progreso: ProgresoGuardado) {
        context.dataStore.edit { preferences ->
            preferences[NIVEL_KEY] = progreso.nivelDesbloqueado
            preferences[ESTRELLAS_KEY] = progreso.estrellas
            preferences[PALABRAS_KEY] = progreso.palabrasCompletadas
        }
    }
}

data class ProgresoGuardado(
    val nivelDesbloqueado: Int = 0,
    val estrellas: Int = 0,
    val palabrasCompletadas: Set<String> = emptySet()
)
