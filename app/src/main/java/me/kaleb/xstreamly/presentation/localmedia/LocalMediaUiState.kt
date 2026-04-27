package me.kaleb.xstreamly.presentation.localmedia

import me.kaleb.xstreamly.domain.model.LocalAudio
import me.kaleb.xstreamly.domain.model.LocalVideo

data class LocalMediaUiState(
    val isLoading: Boolean = true,
    val videos: List<LocalVideo> = emptyList(),
    val audios: List<LocalAudio> = emptyList(),
    val query: String = ""
)

