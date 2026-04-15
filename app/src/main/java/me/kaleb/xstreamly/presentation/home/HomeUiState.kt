package me.kaleb.xstreamly.presentation.home

import me.kaleb.xstreamly.domain.model.StreamPreview

data class HomeUiState(
    val isLoading: Boolean = true,
    val streams: List<StreamPreview> = emptyList(),
    val errorMessage: String? = null
)

