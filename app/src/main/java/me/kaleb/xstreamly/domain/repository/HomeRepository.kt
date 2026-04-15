package me.kaleb.xstreamly.domain.repository

import kotlinx.coroutines.flow.Flow
import me.kaleb.xstreamly.domain.model.StreamPreview

interface HomeRepository {
    fun observeLiveFeed(): Flow<List<StreamPreview>>
}

