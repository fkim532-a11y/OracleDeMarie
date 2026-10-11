package com.kim.oraclemarie.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kim.oraclemarie.data.model.SingleDraw
import kotlinx.coroutines.flow.Flow

@Dao
interface SingleDrawDao {

    @Insert
    suspend fun insert(draw: SingleDraw): Long

    @Query("SELECT DISTINCT cardId FROM single_draws")
    suspend fun getAllDrawnCardIds(): List<Int>

    @Query("SELECT * FROM single_draws ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SingleDraw>>

    @Query("UPDATE single_draws SET userNote = :note WHERE id = :id")
    suspend fun updateNote(id: Int, note: String)
}