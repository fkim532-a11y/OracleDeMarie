package com.kim.oraclemarie.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GalleryUiState(
    val isLoading: Boolean = true,
    val cards: List<Card> = emptyList(),
    val discoveredIds: Set<Int> = emptySet()
)

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = OracleDatabase.getInstance(application)
    private val cardRepository = CardRepository(db)

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val cards = cardRepository.getAllCards().first()
            val discoveredIds = cardRepository.getAllDrawnCardIds().toSet()
            _uiState.update {
                it.copy(isLoading = false, cards = cards, discoveredIds = discoveredIds)
            }
        }
    }
}