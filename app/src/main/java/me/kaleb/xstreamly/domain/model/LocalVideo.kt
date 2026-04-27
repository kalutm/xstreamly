package me.kaleb.xstreamly.domain.model

import android.net.Uri

data class LocalVideo(
    val id: Long,
    val title: String,
    val duration: Long,
    val uri: Uri,
    val thumbnailUri: Uri? = null
)