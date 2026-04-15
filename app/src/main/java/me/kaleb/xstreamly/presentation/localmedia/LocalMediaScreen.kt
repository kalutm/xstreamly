package me.kaleb.xstreamly.presentation.localmedia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LocalMediaScreen(
    viewModel: LocalMediaViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onSearchChanged,
            label = { Text(text = "Search local media") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(text = "Videos", style = MaterialTheme.typography.titleMedium)
            }
            items(state.videoTitles) { title ->
                Text(text = title)
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(text = "Music", style = MaterialTheme.typography.titleMedium)
            }
            items(state.audioTitles) { title ->
                Text(text = title)
            }
            if (state.videoTitles.isEmpty() && state.audioTitles.isEmpty()) {
                item {
                    Text(text = "No local media found")
                }
            }
        }
    }
}





