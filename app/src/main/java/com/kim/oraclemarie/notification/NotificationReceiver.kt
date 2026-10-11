package com.kim.oraclemarie.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kim.oraclemarie.data.SettingsPreferences

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        sendReminderNotification(context)
        val prefs = SettingsPreferences(context)
        scheduleReminder(context, prefs.notificationHour, prefs.notificationMinute)
    }
}
