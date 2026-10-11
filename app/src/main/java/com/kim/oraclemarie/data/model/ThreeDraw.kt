package com.kim.oraclemarie.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "three_draws")
data class ThreeDraw(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val card1Id: Int,   // Corps
    val card2Id: Int,   // Âme
    val card3Id: Int,   // Esprit
    val selectedTheme: String,
    val userNote: String = ""
)