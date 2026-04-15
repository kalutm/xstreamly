package me.kaleb.xstreamly.presentation.golive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GoLiveScreen(
    viewModel: GoLiveViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Live stream setup", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::onTitleChanged,
            label = { Text(text = "Title") }
        )
        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChanged,
            label = { Text(text = "Description") }
        )
        Button(onClick = viewModel::onToggleLive) {
            Text(text = if (state.isLive) "Stop Stream" else "Go Live")
        }
    }
}


