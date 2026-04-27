package me.kaleb.xstreamly.domain.model

import android.net.Uri

data class LocalAudio(
    val id: Long,
    val title: String,
    val artist: String?,
    val duration: Long,
    val uri: Uri
)