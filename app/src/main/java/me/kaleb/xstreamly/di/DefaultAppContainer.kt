package me.kaleb.xstreamly.di

import android.content.ContentResolver
import android.content.Context
import me.kaleb.xstreamly.data.repository.*
import me.kaleb.xstreamly.domain.repository.*

object DefaultAppContainer : AppContainer {

    // We'll initialize this later from Application
    private lateinit var contentResolver: ContentResolver

    // Initialize the container (call this once in Application class)
    fun init(context: Context) {
        contentResolver = context.contentResolver
    }

    override val homeRepository: HomeRepository = HomeRepositoryImpl()

    override val localMediaRepository: LocalMediaRepository by lazy {
        LocalMediaRepositoryImpl(contentResolver)     // ← Now it works!
    }

    override val liveRepository: LiveRepository = LiveRepositoryImpl()

    override val premiereRepository: PremiereRepository = PremiereRepositoryImpl()
}