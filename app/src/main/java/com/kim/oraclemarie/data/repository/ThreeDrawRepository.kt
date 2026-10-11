package com.kim.oraclemarie.data.repository

import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.ThreeDraw
import kotlinx.coroutines.flow.Flow

class ThreeDrawRepository(private val db: OracleDatabase) {

    suspend fun saveThreeDraw(draw: ThreeDraw): Long =
        db.threeDrawDao().insert(draw)

    suspend fun updateNote(id: Int, note: String) =
        db.threeDrawDao().updateNote(id, note)

    fun getAll(): Flow<List<ThreeDraw>> =
        db.threeDrawDao().getAll()
}