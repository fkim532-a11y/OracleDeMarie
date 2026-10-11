package com.kim.oraclemarie.ui.card

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CardDetailUiState(
    val isLoading: Boolean = true,
    val card: Card? = null,
    val selectedTab: Int = 0
)

class CardDetailViewModel(
    application: Application,
    private val cardId: Int,
    initialTab: Int = 0
) : AndroidViewModel(application) {

    private val repository = CardRepository(OracleDatabase.getInstance(application))

    private val _uiState = MutableStateFlow(CardDetailUiState(selectedTab = initialTab))
    val uiState: StateFlow<CardDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val card = repository.getCardById(cardId)
            _uiState.update { it.copy(isLoading = false, card = card) }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    companion object {
        fun factory(cardId: Int, initialTab: Int = 0) = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                CardDetailViewModel(app, cardId, initialTab)
            }
        }
    }
}
