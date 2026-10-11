package com.kim.oraclemarie.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kim.oraclemarie.data.model.Prayer
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayers ORDER BY id ASC")
    fun getAll(): Flow<List<Prayer>>

    @Query("SELECT * FROM prayers WHERE id = :id")
    suspend fun getById(id: Int): Prayer?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(prayers: List<Prayer>)
}
