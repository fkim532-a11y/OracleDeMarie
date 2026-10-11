package com.kim.oraclemarie.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kim.oraclemarie.data.SettingsPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = SettingsPreferences(context)
        if (prefs.notificationsEnabled) {
            scheduleReminder(context, prefs.notificationHour, prefs.notificationMinute)
        }
    }
}
