package me.kaleb.xstreamly.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.kaleb.xstreamly.domain.repository.PremiereRepository

class PremiereRepositoryImpl : PremiereRepository {
    override fun observeScheduledCount(): Flow<Int> = flowOf(0)
}


