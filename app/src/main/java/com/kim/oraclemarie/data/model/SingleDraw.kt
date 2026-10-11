package com.kim.oraclemarie.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "single_draws")
data class SingleDraw(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val cardId: Int,
    val selectedTheme: String,
    val userNote: String = ""
)