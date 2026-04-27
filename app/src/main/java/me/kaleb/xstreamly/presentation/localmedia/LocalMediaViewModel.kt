package me.kaleb.xstreamly.presentation.localmedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import me.kaleb.xstreamly.di.DefaultAppContainer

class LocalMediaViewModel : ViewModel() {
    private val repository = DefaultAppContainer.localMediaRepository

    private val query = MutableStateFlow("")

    val uiState = combine(
        repository.observeVideos(),
        repository.observeAudios(),
        query
    ) { videos, audios, currentQuery ->
        val normalized = currentQuery.trim()
        val filteredVideos = if (normalized.isBlank()) {
            videos
        } else {
            videos.filter { it.title.contains(normalized, ignoreCase = true) }
        }
        val filteredAudios = if (normalized.isBlank()) {
            audios
        } else {
            audios.filter { it.title.contains(normalized, ignoreCase = true) }
        }

        LocalMediaUiState(
            isLoading = false,
            videos = filteredVideos,
            audios = filteredAudios,
            query = currentQuery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LocalMediaUiState()
    )

    fun onSearchChanged(value: String) {
        query.update { value }
    }
}


