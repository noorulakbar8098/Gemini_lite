package com.example.gemini_lite.common

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "user_prefs")

object PreferencesKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
}

suspend fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
    }
}

suspend fun clearLoginState(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(PreferencesKeys.IS_LOGGED_IN)
    }
}

fun isLoggedIn(context: Context): Flow<Boolean> {
    return context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }
}