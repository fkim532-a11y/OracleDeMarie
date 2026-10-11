package com.kim.oraclemarie.data.repository

import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.MarieQuote
import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val db: OracleDatabase) {

    fun getAllQuotes(): Flow<List<MarieQuote>> = db.marieQuoteDao().getAll()

    suspend fun getRandomQuote(): MarieQuote? = db.marieQuoteDao().getRandom()

    suspend fun getQuoteByCategory(category: String): MarieQuote? =
        db.marieQuoteDao().getByCategory(category)
}
