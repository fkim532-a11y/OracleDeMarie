package com.kim.oraclemarie.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marie_quotes")
data class MarieQuote(
    @PrimaryKey val id: Int,
    val textFr: String,
    val textEn: String,
    val category: String
)
