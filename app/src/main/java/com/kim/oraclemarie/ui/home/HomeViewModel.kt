package com.kim.oraclemarie.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.SingleDraw
import com.kim.oraclemarie.data.repository.CardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val card: Card? = null,
    val selectedTheme: String? = null,
    val isFlipped: Boolean = false,
    val shuffleKey: Int = 0,
    val note: String = "",
    val currentDrawId: Int? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CardRepository(OracleDatabase.getInstance(application))

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadNewCard()
    }

    private fun loadNewCard() {
        viewModelScope.launch {
            val cardId = (1..33).random()
            var card: Card? = null
            while (card == null) {
                card = repository.getCardById(cardId)
                if (card == null) delay(300)
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    card = card,
                    selectedTheme = null,
                    isFlipped = false,
                    note = "",
                    currentDrawId = null
                )
            }
        }
    }

    fun resetFlip() {
        viewModelScope.launch {
            val newCardId = (1..33).random()
            val newCard = repository.getCardById(newCardId) ?: return@launch
            _uiState.update {
                it.copy(
                    isFlipped = false,
                    selectedTheme = null,
                    card = newCard,
                    shuffleKey = it.shuffleKey + 1,
                    currentDrawId = null,
                    note = ""
                )
            }
        }
    }

    fun selectTheme(theme: String) {
        val card = _uiState.value.card ?: return
        viewModelScope.launch {
            val drawId = repository.saveSingleDraw(
                SingleDraw(
                    timestamp = System.currentTimeMillis(),
                    cardId = card.id,
                    selectedTheme = theme
                )
            )
            _uiState.update {
                it.copy(
                    selectedTheme = theme,
                    isFlipped = true,
                    currentDrawId = drawId.toInt()
                )
            }
        }
    }

    fun saveNote(text: String) {
        val drawId = _uiState.value.currentDrawId ?: return
        viewModelScope.launch {
            repository.updateSingleDrawNote(drawId, text)
            _uiState.update { it.copy(note = text) }
        }
    }
}