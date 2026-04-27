package me.kaleb.xstreamly.presentation.localmedia

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LocalMediaScreen(
    viewModel: LocalMediaViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    // Permission states
    var hasPermission by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }
    var isPermanentlyDenied by remember { mutableStateOf(false) }

    // Permission Launcher for multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        val videoGranted = result[Manifest.permission.READ_MEDIA_VIDEO] == true
        val audioGranted = result[Manifest.permission.READ_MEDIA_AUDIO] == true
        val storageGranted = result[Manifest.permission.READ_EXTERNAL_STORAGE] == true

        hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            videoGranted && audioGranted
        } else {
            storageGranted
        }

        // Determine rationale vs permanent denial
        if (!hasPermission) {
            val needsRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (result[Manifest.permission.READ_MEDIA_VIDEO] == false ||
                        result[Manifest.permission.READ_MEDIA_AUDIO] == false) &&
                        (context as? android.app.Activity)?.let { activity ->
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                activity, Manifest.permission.READ_MEDIA_VIDEO
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                activity, Manifest.permission.READ_MEDIA_AUDIO
                            )
                        } ?: false
            } else {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    context as? android.app.Activity ?: return@rememberLauncherForActivityResult,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            showRationale = needsRationale
            isPermanentlyDenied = !needsRationale
        } else {
            // Permission granted → reset rationale flags
            showRationale = false
            isPermanentlyDenied = false
        }
    }

    // Helper function to get required permissions based on Android version
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // Initial permission check + auto-request only if needed
    LaunchedEffect(Unit) {
        val requiredPerms = getRequiredPermissions()

        val allGranted = requiredPerms.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            hasPermission = true
        } else {
            // Only request if not granted
            permissionLauncher.launch(requiredPerms)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            // Case 1: Permission fully granted → Show normal content
            hasPermission -> {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::onSearchChanged,
                    label = { Text("Search local media") },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Text("Videos", style = MaterialTheme.typography.titleMedium) }
                    items(state.videos) { video ->
                        Text(text = video.title)
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Music", style = MaterialTheme.typography.titleMedium)
                    }
                    items(state.audios) { video ->
                        Text(text = video.title)
                    }

                    if (state.videos.isEmpty() && state.audios.isEmpty()) {
                        item { Text("No local media found") }
                    }
                }
            }

            // Case 2: Show rationale (user denied once, but we can still ask)
            showRationale -> {
                PermissionRationaleUI(
                    onGrantClick = {
                        permissionLauncher.launch(getRequiredPermissions())
                    }
                )
            }

            // Case 3: Permanently denied → Guide user to Settings
            isPermanentlyDenied -> {
                PermissionPermanentlyDeniedUI(
                    onGoToSettingsClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // Case 4: First time or no permission yet
            else -> {
                PermissionRequiredUI(
                    onGrantClick = {
                        permissionLauncher.launch(getRequiredPermissions())
                    }
                )
            }
        }
    }
}

// Simple UI for initial permission request
@Composable
private fun PermissionRequiredUI(onGrantClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Permission required",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "XStreamly needs access to your local videos and music to play them.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onGrantClick) {
                Text("Grant Permission")
            }
        }
    }
}

// UI when shouldShowRequestPermissionRationale() == true
@Composable
private fun PermissionRationaleUI(onGrantClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Why we need permission",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "To show and play your local videos and audio files stored on this device, " +
                        "XStreamly requires access to your media library.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onGrantClick) {
                Text("Grant Permission")
            }
        }
    }
}

// UI for permanently denied case
@Composable
private fun PermissionPermanentlyDeniedUI(onGoToSettingsClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Permission denied",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "You have permanently denied media access.\n\n" +
                        "Please go to App Settings > Permissions and enable " +
                        "Photos and videos + Music and audio.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onGoToSettingsClick) {
                Text("Open App Settings")
            }
        }
    }
}