package com.photoapp.auth

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.authDataStore by preferencesDataStore(name = "auth")
private val TOKEN_KEY = stringPreferencesKey("token")

data class AuthUiState(
    val isLoggedIn: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getApplication<Application>().authDataStore.data
                .catch { throwable ->
                    if (throwable is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw throwable
                    }
                }
                .map { preferences -> !preferences[TOKEN_KEY].isNullOrBlank() }
                .collectLatest { loggedIn ->
                    _uiState.value = AuthUiState(isLoggedIn = loggedIn)
                }
        }
    }

    fun login() {
        viewModelScope.launch {
            getApplication<Application>().authDataStore.edit { preferences ->
                preferences[TOKEN_KEY] = "demo-token"
            }
        }
    }
}
