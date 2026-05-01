package me.kaleb.xstreamly.presentation.localmedia

import android.content.ContentUris
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import me.kaleb.xstreamly.R
import me.kaleb.xstreamly.domain.model.LocalAudio

// Simple UI for initial permission request
@Composable
fun PermissionRequiredUI(onGrantClick: () -> Unit) {
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
fun PermissionRationaleUI(onGrantClick: () -> Unit) {
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
fun PermissionPermanentlyDeniedUI(onGoToSettingsClick: () -> Unit) {
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
        val indicatorOffset by transition.animateDp(label = "Offset") { selected ->
            tabWidth * options.indexOf(selected)
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(x = indicatorOffset.roundToPx(), y = 0)
                }
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
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun VideoThumbnail(
    videoUri: Uri,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data(videoUri)
        .build()

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription ?: "Video thumbnail",
        modifier = modifier,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        error = painterResource(R.drawable.ic_launcher_foreground)
    )
}

@Composable
fun AudioThumbnail(
    audio: LocalAudio,
    modifier: Modifier = Modifier
) {
    // Album art URI
    val albumArtUri = audio.albumId?.let { albumId ->
        ContentUris.withAppendedId(
            "content://media/external/audio/albumart".toUri(),
            albumId
        )
    }

    AsyncImage(
        model = albumArtUri ?: R.drawable.ic_launcher_foreground,   // fallback
        contentDescription = "Album art for ${audio.title}",
        modifier = modifier,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        error = painterResource(R.drawable.ic_launcher_foreground)
    )
}