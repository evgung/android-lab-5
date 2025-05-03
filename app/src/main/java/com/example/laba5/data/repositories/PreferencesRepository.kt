package com.example.laba5.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.coroutineContext

class PreferencesRepository (
    private val dataStore: DataStore<Preferences>
) {
    private val SEARCH_QUERY = stringPreferencesKey("search_query")

    suspend fun saveSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY] = query
        }
    }

    val searchQuery: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SEARCH_QUERY] ?: ""
        }
}