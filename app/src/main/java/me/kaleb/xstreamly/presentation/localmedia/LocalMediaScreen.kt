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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import android.content.ContentUris
import androidx.core.net.toUri
import me.kaleb.xstreamly.R
import me.kaleb.xstreamly.domain.model.LocalAudio

private const val PERMISSION_READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO"
private const val PERMISSION_READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO"

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
    var isVideo by remember {mutableStateOf(false)}

    // Permission Launcher for multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        val videoGranted = result[PERMISSION_READ_MEDIA_VIDEO] == true
        val audioGranted = result[PERMISSION_READ_MEDIA_AUDIO] == true
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
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_READ_MEDIA_VIDEO) ||
                            // OR check if we should explain for AUDIO permission
                            ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_READ_MEDIA_AUDIO)
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
                PERMISSION_READ_MEDIA_VIDEO,
                PERMISSION_READ_MEDIA_AUDIO
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
                SlidingSwitch(
                    listOf("Audio", "Video"),
                    if (isVideo) "Video"  else "Audio"
                ) { selected ->
                    isVideo = if (selected == "Video") true else false
                }

                if(isVideo){
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            gridItems(state.videos, key = { it.id }) { video ->
                                Card {
                                    Column {
                                        VideoThumbnail(
                                            videoUri = video.uri,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(16f/9f)     // Good for video thumbnails
                                        )
                                        Text(
                                            text = video.title,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    if(state.videos.isEmpty()){
                        Text("No video found")
                    }
                } else{
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    LazyColumn() {
                        items(state.audios) { audio ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AudioThumbnail(
                                    audio = audio,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(text = audio.title, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = audio.artist ?: "Unknown Artist",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    if(state.audios.isEmpty()){
                        Text("No audio found")
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

