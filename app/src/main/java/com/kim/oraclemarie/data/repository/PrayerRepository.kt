package com.kim.oraclemarie.data.repository

import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Prayer
import kotlinx.coroutines.flow.Flow

class PrayerRepository(private val db: OracleDatabase) {

    fun getAllPrayers(): Flow<List<Prayer>> = db.prayerDao().getAll()

    suspend fun getPrayerById(id: Int): Prayer? = db.prayerDao().getById(id)
}
