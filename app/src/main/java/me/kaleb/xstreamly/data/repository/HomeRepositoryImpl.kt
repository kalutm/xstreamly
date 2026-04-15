package me.kaleb.xstreamly.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.kaleb.xstreamly.domain.model.StreamPreview
import me.kaleb.xstreamly.domain.repository.HomeRepository

class HomeRepositoryImpl : HomeRepository {
    override fun observeLiveFeed(): Flow<List<StreamPreview>> =
        flowOf(
            listOf(
                StreamPreview(
                    id = "sample-room-1",
                    title = "Compose Basics Live",
                    streamerName = "Kaleb",
                    isLive = true
                ),
                StreamPreview(
                    id = "sample-room-2",
                    title = "Media3 Playback Setup",
                    streamerName = "XStreamly",
                    isLive = true
                )
            )
        )
}


