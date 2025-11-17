package com.example.classifyleaves

import android.app.Application
import com.example.classifyleaves.di.leafModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ClassifyLeavesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ClassifyLeavesApp)
            modules(leafModule)
        }
    }
} 