package me.kaleb.xstreamly.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocalMediaRepository {
    fun observeVideoTitles(): Flow<List<String>>
    fun observeAudioTitles(): Flow<List<String>>
}

