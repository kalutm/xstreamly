package me.kaleb.xstreamly.presentation.localmedia

data class LocalMediaUiState(
    val isLoading: Boolean = true,
    val videoTitles: List<String> = emptyList(),
    val audioTitles: List<String> = emptyList(),
    val query: String = ""
)

