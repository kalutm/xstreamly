package me.kaleb.xstreamly.presentation.premiere

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.kaleb.xstreamly.di.DefaultAppContainer

class PremiereViewModel : ViewModel() {
    private val repository = DefaultAppContainer.premiereRepository

    val uiState = repository.observeScheduledCount()
        .map { count ->
            PremiereUiState(
                scheduledCount = count,
                nextPremiereLabel = if (count == 0) {
                    "No premiere scheduled"
                } else {
                    "Next premiere in 2 days"
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PremiereUiState()
        )
}


