package com.kim.oraclemarie.ui.paroles

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kim.oraclemarie.data.db.OracleDatabase
import com.kim.oraclemarie.data.model.MarieQuote
import com.kim.oraclemarie.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParolesMarieUiState(
    val isLoading: Boolean = true,
    val quote: MarieQuote? = null
)

class ParolesMarieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuoteRepository(OracleDatabase.getInstance(application))

    private val _uiState = MutableStateFlow(ParolesMarieUiState())
    val uiState: StateFlow<ParolesMarieUiState> = _uiState.asStateFlow()

    init {
        loadRandom()
    }

    fun loadRandom() {
        viewModelScope.launch {
            val quote = repository.getRandomQuote()
            _uiState.update { it.copy(isLoading = false, quote = quote) }
        }
    }
}
