package me.kaleb.xstreamly.presentation.localmedia

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.TextStyle

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

        // Determine whether we should show rationale or treat as permanently denied
        if (!hasPermission) {

            // We only enter this block if at least one required permission was denied.
            // Now we need to decide: Should we show explanation (rationale) or tell user to go to Settings?

            val needsRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                // For Android 13+: We requested TWO permissions (VIDEO + AUDIO)
                // We should show rationale if:
                //   1. At least one permission was denied in this request, AND
                //   2. The system says we can still ask again (shouldShowRequestPermissionRationale = true)

                val shouldShowExplanation = (context as? android.app.Activity)?.let { activity ->
                    // Check if we should explain for VIDEO permission
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_MEDIA_VIDEO) ||
                            // OR check if we should explain for AUDIO permission
                            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_MEDIA_AUDIO)
                } ?: false

                shouldShowExplanation

            } else {
                // For Android 12 and below: We only requested one permission (READ_EXTERNAL_STORAGE)
                // Since we're already inside !hasPermission, we know it was denied.
                // So we just need to check if we should show rationale.

                ActivityCompat.shouldShowRequestPermissionRationale(
                    context as? android.app.Activity ?: return@rememberLauncherForActivityResult,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            // Final decision based on the above logic
            showRationale = needsRationale
            isPermanentlyDenied = !needsRationale

        }
        else {
            // Permission(s) were granted successfully
            // Reset both flags so we don't show permission UI anymore
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
                    item {
                        Text("Videos", style = MaterialTheme.typography.titleMedium)
                    }
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

// Ui for switching button between audio and video
@Composable
fun SlidingSwitch(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val transition = updateTransition(targetState = selectedOption, label = "SwitchTransition")

    // Determine which index is selected to calculate the offset
    val selectedIndex = options.indexOf(selectedOption)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5)) // Light grey background
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(4.dp) // Inner spacing for the slider
    ) {
        val maxWidth = maxWidth
        val tabWidth = maxWidth / options.size

        // The Animated Sliding Background
        val indicatorOffset by transition.animateDp(label = "Offset") { _ ->
            tabWidth * selectedIndex
        }

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1976D2)) // Blue active color
        )

        // The Clickable Labels
        Row(modifier = Modifier.fillMaxSize()) {
            options.forEach { option ->
                val isSelected = option == selectedOption

                // Animate text color for a smoother feel
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.Black,
                    animationSpec = tween(durationMillis = 300)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Remove ripple to keep it clean
                        ) { onOptionSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                    )
                }
            }
        }
    }
}