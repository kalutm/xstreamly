package me.kaleb.xstreamly

import android.app.Application
import me.kaleb.xstreamly.di.DefaultAppContainer

class XStreamlyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Important: Initialize the container here
        DefaultAppContainer.init(this)
    }
}