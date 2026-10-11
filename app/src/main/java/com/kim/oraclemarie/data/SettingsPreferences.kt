package com.kim.oraclemarie.data

import android.content.Context

class SettingsPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("oracle_settings", Context.MODE_PRIVATE)

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notif_enabled", false)
        set(value) { prefs.edit().putBoolean("notif_enabled", value).apply() }

    var notificationHour: Int
        get() = prefs.getInt("notif_hour", 8)
        set(value) { prefs.edit().putInt("notif_hour", value).apply() }

    var notificationMinute: Int
        get() = prefs.getInt("notif_minute", 0)
        set(value) { prefs.edit().putInt("notif_minute", value).apply() }

    var language: String
        get() = prefs.getString("language", "fr") ?: "fr"
        set(value) { prefs.edit().putString("language", value).apply() }

    var onboardingDone: Boolean
        get() = prefs.getBoolean("onboarding_done", false)
        set(value) { prefs.edit().putBoolean("onboarding_done", value).apply() }
}
