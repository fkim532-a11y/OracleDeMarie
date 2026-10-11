package com.kim.oraclemarie.ui.journal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.SingleDraw
import com.kim.oraclemarie.data.model.ThreeDraw
import com.kim.oraclemarie.data.repository.CardRepository
import com.kim.oraclemarie.data.repository.ThreeDrawRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class JournalEntry {
    abstract val timestamp: Long

    data class Single(
        val draw: SingleDraw,
        val card: Card?
    ) : JournalEntry() {
        override val timestamp: Long get() = draw.timestamp
    }

    data class Three(
        val draw: ThreeDraw,
        val card1: Card?,
        val card2: Card?,
        val card3: Card?
    ) : JournalEntry() {
        override val timestamp: Long get() = draw.timestamp
    }
}

data class JournalUiState(
    val isLoading: Boolean = true,
    val entries: List<JournalEntry> = emptyList()
)

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = OracleDatabase.getInstance(application)
    private val cardRepository = CardRepository(db)
    private val threeDrawRepository = ThreeDrawRepository(db)

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val cardsMap = cardRepository.getAllCards().first().associateBy { it.id }
            combine(
                cardRepository.getAllSingleDraws(),
                threeDrawRepository.getAll()
            ) { singles, threes ->
                val singleEntries = singles.map { draw ->
                    JournalEntry.Single(draw = draw, card = cardsMap[draw.cardId])
                }
                val threeEntries = threes.map { draw ->
                    JournalEntry.Three(
                        draw = draw,
                        card1 = cardsMap[draw.card1Id],
                        card2 = cardsMap[draw.card2Id],
                        card3 = cardsMap[draw.card3Id]
                    )
                }
                (singleEntries + threeEntries).sortedByDescending { it.timestamp }
            }.collect { entries ->
                _uiState.update { it.copy(isLoading = false, entries = entries) }
            }
        }
    }
}
