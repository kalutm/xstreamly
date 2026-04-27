package me.kaleb.xstreamly.domain.repository

import kotlinx.coroutines.flow.Flow
import me.kaleb.xstreamly.domain.model.LocalAudio
import me.kaleb.xstreamly.domain.model.LocalVideo

interface LocalMediaRepository {
    /** Returns a Flow that emits the list of videos whenever it changes */
    fun observeVideos(): Flow<List<LocalVideo>>

    /** Returns a Flow that emits the list of audio files */
    fun observeAudios(): Flow<List<LocalAudio>>
}

