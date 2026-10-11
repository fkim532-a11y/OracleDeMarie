package com.kim.oraclemarie.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayers")
data class Prayer(
    @PrimaryKey val id: Int,
    val titleFr: String,
    val titleEn: String,
    val theme: String,
    val type: String,
    val textFr: String,
    val textEn: String,
    val durationMinutes: Int
)
