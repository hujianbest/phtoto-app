package com.photoapp.auth

import android.app.Application
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.photoapp.network.ApiClient
import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val TOKEN_KEY = stringPreferencesKey("token")
private val EMAIL_KEY = stringPreferencesKey("email")
private val CHALLENGE_JOINED_AT_KEY = stringPreferencesKey("challenge_joined_at")
private val REPORT_HISTORY_KEY = stringPreferencesKey("report_history")

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val challengeJoinedAt: String? = null,
    val reportHistory: List<String> = emptyList()
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
                .map { preferences ->
                    val reportHistory = decodeReportHistory(preferences[REPORT_HISTORY_KEY])
                    AuthUiState(
                        isLoggedIn = !preferences[TOKEN_KEY].isNullOrBlank(),
                        isLoading = _uiState.value.isLoading,
                        errorMessage = _uiState.value.errorMessage,
                        email = preferences[EMAIL_KEY].orEmpty(),
                        challengeJoinedAt = preferences[CHALLENGE_JOINED_AT_KEY],
                        reportHistory = reportHistory
                    )
                }
                .collectLatest { state ->
                    _uiState.value = state
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val safeEmail = email.trim()
            val safePassword = password.trim()
            if (safeEmail.isBlank() || safePassword.length < 8) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "请输入有效邮箱与至少 8 位密码。"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val token = runCatching {
                ApiClient.registerOrLogin(safeEmail, safePassword)
            }.getOrNull()

            if (!token.isNullOrBlank()) {
                getApplication<Application>().authDataStore.edit { preferences ->
                    preferences[TOKEN_KEY] = token
                    preferences[EMAIL_KEY] = safeEmail
                }
                syncReportHistoryFromRemote()
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                return@launch
            }

            // Keep app usable in local-offline mode when backend isn't available.
            getApplication<Application>().authDataStore.edit { preferences ->
                preferences[TOKEN_KEY] = "offline-demo-token"
                preferences[EMAIL_KEY] = safeEmail
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "后端不可达，已进入离线演示模式。"
            )
        }
    }

    fun joinWeeklyChallenge() {
        viewModelScope.launch {
            val email = _uiState.value.email
            val joinedAtFromApi = if (email.isNotBlank()) {
                runCatching { ApiClient.joinWeeklyChallenge(email) }.getOrNull()
            } else {
                null
            }
            getApplication<Application>().authDataStore.edit { preferences ->
                if (preferences[CHALLENGE_JOINED_AT_KEY].isNullOrBlank()) {
                    preferences[CHALLENGE_JOINED_AT_KEY] = joinedAtFromApi ?: Instant.now().toString()
                }
            }
        }
    }

    fun addReportHistory(postId: String, reason: String) {
        viewModelScope.launch {
            val safePostId = postId.trim().ifEmpty { "unknown-post" }
            val safeReason = reason.trim().ifEmpty { "未填写原因" }.replace("\n", " ")
            val entry = "${Instant.now()} | $safePostId | $safeReason"
            getApplication<Application>().authDataStore.edit { preferences ->
                val current = decodeReportHistory(preferences[REPORT_HISTORY_KEY]).toMutableList()
                current.add(0, entry)
                preferences[REPORT_HISTORY_KEY] = encodeReportHistory(current.take(20))
            }
        }
    }

    fun syncReportHistoryFromRemote() {
        viewModelScope.launch {
            val email = _uiState.value.email.trim()
            if (email.isBlank()) {
                return@launch
            }
            val remoteHistory = runCatching {
                ApiClient.fetchReportHistory(email)
            }.getOrDefault(emptyList())
            if (remoteHistory.isEmpty()) {
                return@launch
            }
            getApplication<Application>().authDataStore.edit { preferences ->
                preferences[REPORT_HISTORY_KEY] = encodeReportHistory(remoteHistory.take(20))
            }
        }
    }

    private fun encodeReportHistory(items: List<String>): String =
        items.joinToString(separator = "\n")

    private fun decodeReportHistory(raw: String?): List<String> =
        raw.orEmpty()
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
