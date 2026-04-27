package me.kaleb.xstreamly.di

import android.content.ContentResolver
import me.kaleb.xstreamly.domain.repository.*

interface AppContainer {
    val homeRepository: HomeRepository
    val localMediaRepository: LocalMediaRepository
    val liveRepository: LiveRepository
    val premiereRepository: PremiereRepository
}