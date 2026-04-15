package me.kaleb.xstreamly.domain.repository

import kotlinx.coroutines.flow.Flow

interface PremiereRepository {
    fun observeScheduledCount(): Flow<Int>
}

