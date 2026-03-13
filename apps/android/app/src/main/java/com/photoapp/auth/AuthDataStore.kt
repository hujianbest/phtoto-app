package com.photoapp.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

val Context.authDataStore by preferencesDataStore(name = "auth")

suspend fun clearAuthDataStore(context: Context) {
    context.authDataStore.edit { it.clear() }
}
