package me.kaleb.xstreamly.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.kaleb.xstreamly.domain.repository.LiveRepository

class LiveRepositoryImpl : LiveRepository {
    override fun observeLiveStatus(): Flow<Boolean> = flowOf(false)
}


