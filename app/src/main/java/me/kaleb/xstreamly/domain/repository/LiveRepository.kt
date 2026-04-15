package me.kaleb.xstreamly.domain.repository

import kotlinx.coroutines.flow.Flow

interface LiveRepository {
    fun observeLiveStatus(): Flow<Boolean>
}

