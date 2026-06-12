package com.example.typetrainer.data.repositories

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesRepository(context: Context) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val POKEMON_GO_THEME = booleanPreferencesKey("pokemon_go_theme")
    }

    val pokemonGoTheme: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.POKEMON_GO_THEME] ?: false
        }

    suspend fun setPokemonGoTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.POKEMON_GO_THEME] = enabled
        }
    }
}