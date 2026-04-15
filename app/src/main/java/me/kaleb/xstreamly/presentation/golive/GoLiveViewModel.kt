package me.kaleb.xstreamly.presentation.golive

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.kaleb.xstreamly.di.DefaultAppContainer

class GoLiveViewModel : ViewModel() {
    private val repository = DefaultAppContainer.liveRepository

    private val _uiState = MutableStateFlow(GoLiveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        repository.observeLiveStatus()
    }

    fun onTitleChanged(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onToggleLive() {
        _uiState.update { it.copy(isLive = !it.isLive) }
    }
}


