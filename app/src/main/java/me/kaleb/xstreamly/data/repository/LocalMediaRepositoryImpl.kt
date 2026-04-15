package me.kaleb.xstreamly.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.kaleb.xstreamly.domain.repository.LocalMediaRepository

class LocalMediaRepositoryImpl : LocalMediaRepository {
    override fun observeVideoTitles(): Flow<List<String>> =
        flowOf(listOf("Welcome clip", "Compose tutorial"))

    override fun observeAudioTitles(): Flow<List<String>> =
        flowOf(listOf("Ambient track", "Podcast sample"))
}


