package com.kim.oraclemarie.data.repository

import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.SingleDraw
import kotlinx.coroutines.flow.Flow

class CardRepository(private val db: OracleDatabase) {

    fun getAllCards(): Flow<List<Card>> = db.cardDao().getAll()

    fun getCardsByPart(part: Int): Flow<List<Card>> = db.cardDao().getByPart(part)

    suspend fun getCardById(id: Int): Card? = db.cardDao().getById(id)

    // ─── SingleDraw ──────────────────────────────────────────────────────────

    suspend fun saveSingleDraw(draw: SingleDraw): Long =
        db.singleDrawDao().insert(draw)

    suspend fun updateSingleDrawNote(id: Int, note: String) =
        db.singleDrawDao().updateNote(id, note)

    fun getAllSingleDraws(): Flow<List<SingleDraw>> =
        db.singleDrawDao().getAll()

    suspend fun getAllDrawnCardIds(): List<Int> =
        db.singleDrawDao().getAllDrawnCardIds() +
        db.threeDrawDao().getAllDrawnCardIds()
}