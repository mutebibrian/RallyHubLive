package com.waltsrally.rallyhublive.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waltsrally.rallyhublive.data.api.RetrofitClient
import com.waltsrally.rallyhublive.data.models.LoginRequest
import com.waltsrally.rallyhublive.data.models.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val android.content.Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    private val KEY_TOKEN   = stringPreferencesKey("token")
    private val KEY_NAME    = stringPreferencesKey("name")
    private val KEY_ROLE    = stringPreferencesKey("role")
    private val KEY_CAR     = stringPreferencesKey("car_number")
    private val KEY_ID      = stringPreferencesKey("user_id")

    private val _user = MutableStateFlow<UserInfo?>(null)
    val user: StateFlow<UserInfo?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch { restoreSession() }
    }

    private suspend fun restoreSession() {
        val prefs = dataStore.data.first()
        val token = prefs[KEY_TOKEN] ?: return
        _user.value = UserInfo(
            id        = prefs[KEY_ID]?.toIntOrNull() ?: 0,
            name      = prefs[KEY_NAME] ?: "",
            role      = prefs[KEY_ROLE] ?: "",
            carNumber = prefs[KEY_CAR],
            token     = token
        )
    }

    fun login(username: String, password: String, type: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resp = RetrofitClient.api.login(body = LoginRequest(username = username, password = password, type = type))
                if (resp.ok && resp.user != null) {
                    _user.value = resp.user
                    dataStore.edit { prefs ->
                        prefs[KEY_TOKEN] = resp.user.token
                        prefs[KEY_NAME]  = resp.user.name
                        prefs[KEY_ROLE]  = resp.user.role
                        prefs[KEY_ID]    = resp.user.id.toString()
                        if (resp.user.carNumber != null) prefs[KEY_CAR] = resp.user.carNumber
                    }
                } else {
                    _error.value = resp.error ?: "Invalid credentials"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.edit { it.clear() }
            _user.value = null
        }
    }

    fun clearError() { _error.value = null }
}
