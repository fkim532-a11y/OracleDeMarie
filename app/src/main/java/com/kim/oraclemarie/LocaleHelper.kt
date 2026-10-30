package com.kim.oraclemarie

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

// Applique la langue choisie dans À propos & Soutenir (et non celle du téléphone).
// Utilisé par MainActivity et par la notification de rappel.
fun localizedContext(base: Context): Context {
    val prefs = base.getSharedPreferences("oracle_settings", Context.MODE_PRIVATE)
    val lang = prefs.getString("language", "fr") ?: "fr"
    val locale = Locale.forLanguageTag(lang)
    Locale.setDefault(locale)
    val config = Configuration(base.resources.configuration)
    config.setLocale(locale)
    return base.createConfigurationContext(config)
}
