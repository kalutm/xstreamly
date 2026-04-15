package me.kaleb.xstreamly.di

import me.kaleb.xstreamly.data.repository.HomeRepositoryImpl
import me.kaleb.xstreamly.data.repository.LiveRepositoryImpl
import me.kaleb.xstreamly.data.repository.LocalMediaRepositoryImpl
import me.kaleb.xstreamly.data.repository.PremiereRepositoryImpl
import me.kaleb.xstreamly.domain.repository.HomeRepository
import me.kaleb.xstreamly.domain.repository.LiveRepository
import me.kaleb.xstreamly.domain.repository.LocalMediaRepository
import me.kaleb.xstreamly.domain.repository.PremiereRepository

interface AppContainer {
    val homeRepository: HomeRepository
    val localMediaRepository: LocalMediaRepository
    val liveRepository: LiveRepository
    val premiereRepository: PremiereRepository
}

object DefaultAppContainer : AppContainer {
    override val homeRepository: HomeRepository = HomeRepositoryImpl()
    override val localMediaRepository: LocalMediaRepository = LocalMediaRepositoryImpl()
    override val liveRepository: LiveRepository = LiveRepositoryImpl()
    override val premiereRepository: PremiereRepository = PremiereRepositoryImpl()
}

