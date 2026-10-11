package com.kim.oraclemarie.data.model

import java.util.Locale

private val isFrench: Boolean
    get() = Locale.getDefault().language == "fr"

// ─── Card ────────────────────────────────────────────────────────────────────

val Card.localeName: String get() = if (isFrench) nameFr else nameEn
val Card.localeKeyword: String get() = if (isFrench) keyword else keywordEn
val Card.localeSituation: String get() = if (isFrench) situationFr else situationEn
val Card.localeConseil: String get() = if (isFrench) conseilFr else conseilEn
val Card.localeAmour: String get() = if (isFrench) amourFr else amourEn
val Card.localeConseilAmour: String get() = if (isFrench) conseilAmourFr else conseilAmourEn
val Card.localeTravail: String get() = if (isFrench) travailFr else travailEn
val Card.localeConseilTravail: String get() = if (isFrench) conseilTravailFr else conseilTravailEn
val Card.localeSante: String get() = if (isFrench) santeFr else santeEn
val Card.localeConseilSante: String get() = if (isFrench) conseilSanteFr else conseilSanteEn
val Card.localeFamille: String get() = if (isFrench) familleFr else familleEn
val Card.localeConseilFamille: String get() = if (isFrench) conseilFamilleFr else conseilFamilleEn

fun Card.localeSituationForTheme(theme: String): String = when (theme) {
    "amour"   -> localeAmour
    "travail" -> localeTravail
    "sante"   -> localeSante
    "famille" -> localeFamille
    else      -> localeSituation
}

fun Card.localeConseilForTheme(theme: String): String = when (theme) {
    "amour"   -> localeConseilAmour
    "travail" -> localeConseilTravail
    "sante"   -> localeConseilSante
    "famille" -> localeConseilFamille
    else      -> localeConseil
}

// ─── Prayer ──────────────────────────────────────────────────────────────────

val Prayer.localeTitle: String get() = if (isFrench) titleFr else titleEn
val Prayer.localeText: String get() = if (isFrench) textFr else textEn

// ─── MarieQuote ──────────────────────────────────────────────────────────────

val MarieQuote.localeText: String get() = if (isFrench) textFr else textEn
