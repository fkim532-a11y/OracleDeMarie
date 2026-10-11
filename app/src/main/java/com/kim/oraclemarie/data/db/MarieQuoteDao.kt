package com.kim.oraclemarie.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kim.oraclemarie.data.model.MarieQuote
import kotlinx.coroutines.flow.Flow

@Dao
interface MarieQuoteDao {
    @Query("SELECT * FROM marie_quotes ORDER BY id ASC")
    fun getAll(): Flow<List<MarieQuote>>

    @Query("SELECT * FROM marie_quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandom(): MarieQuote?

    @Query("SELECT * FROM marie_quotes WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    suspend fun getByCategory(category: String): MarieQuote?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(quotes: List<MarieQuote>)
}
