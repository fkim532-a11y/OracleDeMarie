package com.kim.oraclemarie.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kim.oraclemarie.data.SettingsPreferences
import com.kim.oraclemarie.notification.cancelReminder
import com.kim.oraclemarie.notification.scheduleReminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val notificationsEnabled: Boolean = false,
    val notificationHour: Int = 8,
    val notificationMinute: Int = 0,
    val language: String = "fr"
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = SettingsPreferences(app)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            notificationsEnabled = prefs.notificationsEnabled,
            notificationHour = prefs.notificationHour,
            notificationMinute = prefs.notificationMinute,
            language = prefs.language
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.notificationsEnabled = enabled
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        val ctx = getApplication<Application>()
        if (enabled) scheduleReminder(ctx, prefs.notificationHour, prefs.notificationMinute)
        else cancelReminder(ctx)
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        prefs.notificationHour = hour
        prefs.notificationMinute = minute
        _uiState.value = _uiState.value.copy(notificationHour = hour, notificationMinute = minute)
        if (prefs.notificationsEnabled) scheduleReminder(getApplication(), hour, minute)
    }

    fun setLanguage(lang: String) {
        prefs.language = lang
        _uiState.value = _uiState.value.copy(language = lang)
    }
}
