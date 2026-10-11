package com.kim.oraclemarie.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: Int,
    val nameFr: String,
    val nameEn: String,
    val keyword: String,
    val keywordEn: String,
    val imageName: String,
    val partNumber: Int,
    val situationFr: String,
    val situationEn: String,
    val conseilFr: String,
    val conseilEn: String,
    val amourFr: String,
    val amourEn: String,
    val conseilAmourFr: String,
    val conseilAmourEn: String,
    val travailFr: String,
    val travailEn: String,
    val conseilTravailFr: String,
    val conseilTravailEn: String,
    val santeFr: String,
    val santeEn: String,
    val conseilSanteFr: String,
    val conseilSanteEn: String,
    val familleFr: String,
    val familleEn: String,
    val conseilFamilleFr: String,
    val conseilFamilleEn: String
)
