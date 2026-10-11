package com.kim.oraclemarie.ui.troiscartes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.ThreeDraw
import com.kim.oraclemarie.data.repository.CardRepository
import com.kim.oraclemarie.data.repository.ThreeDrawRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TroisCartesUiState(
    val isLoading: Boolean = true,
    val card1: Card? = null,   // Corps
    val card2: Card? = null,   // Âme
    val card3: Card? = null,   // Esprit
    val selectedTheme: String? = null,
    val flipped1: Boolean = false,
    val flipped2: Boolean = false,
    val flipped3: Boolean = false,
    val shuffleKey: Int = 0,
    val currentDrawId: Int? = null,
    val note: String = ""
)

class TroisCartesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = OracleDatabase.getInstance(application)
    private val cardRepository = CardRepository(db)
    private val threeDrawRepository = ThreeDrawRepository(db)

    private val _uiState = MutableStateFlow(TroisCartesUiState())
    val uiState: StateFlow<TroisCartesUiState> = _uiState.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            val (id1, id2, id3) = (1..33).shuffled().take(3)
            var c1: Card? = null
            var c2: Card? = null
            var c3: Card? = null
            while (c1 == null || c2 == null || c3 == null) {
                if (c1 == null) c1 = cardRepository.getCardById(id1)
                if (c2 == null) c2 = cardRepository.getCardById(id2)
                if (c3 == null) c3 = cardRepository.getCardById(id3)
                if (c1 == null || c2 == null || c3 == null) delay(300)
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    card1 = c1, card2 = c2, card3 = c3,
                    selectedTheme = null,
                    flipped1 = false, flipped2 = false, flipped3 = false,
                    currentDrawId = null,
                    note = ""
                )
            }
        }
    }

    fun drawNewCards() {
        viewModelScope.launch {
            val (id1, id2, id3) = (1..33).shuffled().take(3)
            var c1: Card? = null
            var c2: Card? = null
            var c3: Card? = null
            while (c1 == null || c2 == null || c3 == null) {
                if (c1 == null) c1 = cardRepository.getCardById(id1)
                if (c2 == null) c2 = cardRepository.getCardById(id2)
                if (c3 == null) c3 = cardRepository.getCardById(id3)
                if (c1 == null || c2 == null || c3 == null) delay(300)
            }
            _uiState.update {
                it.copy(
                    card1 = c1, card2 = c2, card3 = c3,
                    selectedTheme = null,
                    flipped1 = false, flipped2 = false, flipped3 = false,
                    shuffleKey = it.shuffleKey + 1,
                    currentDrawId = null,
                    note = ""
                )
            }
        }
    }

    fun selectTheme(theme: String) {
        val c1 = _uiState.value.card1 ?: return
        val c2 = _uiState.value.card2 ?: return
        val c3 = _uiState.value.card3 ?: return
        // Mise à jour immédiate pour déclencher l'animation sans délai
        _uiState.update { it.copy(selectedTheme = theme) }
        viewModelScope.launch {
            val drawId = threeDrawRepository.saveThreeDraw(
                ThreeDraw(
                    timestamp = System.currentTimeMillis(),
                    card1Id = c1.id,
                    card2Id = c2.id,
                    card3Id = c3.id,
                    selectedTheme = theme
                )
            )
            _uiState.update { it.copy(currentDrawId = drawId.toInt()) }
        }
    }

    fun flipCard(position: Int) {
        _uiState.update {
            when (position) {
                1 -> it.copy(flipped1 = true)
                2 -> it.copy(flipped2 = true)
                3 -> it.copy(flipped3 = true)
                else -> it
            }
        }
    }

    fun saveNote(text: String) {
        val drawId = _uiState.value.currentDrawId ?: return
        viewModelScope.launch {
            threeDrawRepository.updateNote(drawId, text)
            _uiState.update { it.copy(note = text) }
        }
    }
}