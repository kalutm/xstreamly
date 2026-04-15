package me.kaleb.xstreamly.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.kaleb.xstreamly.di.DefaultAppContainer

class HomeViewModel : ViewModel() {
    private val repository = DefaultAppContainer.homeRepository

    val uiState = repository.observeLiveFeed()
        .map { streams ->
            HomeUiState(
                isLoading = false,
                streams = streams
            )
        }
        .catch {
            emit(
                HomeUiState(
                    isLoading = false,
                    errorMessage = "Unable to load live feed"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )
}


