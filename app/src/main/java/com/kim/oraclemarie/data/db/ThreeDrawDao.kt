package com.kim.oraclemarie.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kim.oraclemarie.data.model.ThreeDraw
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreeDrawDao {

    @Insert
    suspend fun insert(draw: ThreeDraw): Long

    @Query("SELECT card1Id FROM three_draws UNION SELECT card2Id FROM three_draws UNION SELECT card3Id FROM three_draws")
    suspend fun getAllDrawnCardIds(): List<Int>

    @Query("SELECT * FROM three_draws ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ThreeDraw>>

    @Query("UPDATE three_draws SET userNote = :note WHERE id = :id")
    suspend fun updateNote(id: Int, note: String)
}