package com.example.photoalbum

import androidx.multidex.MultiDexApplication
import com.example.photoalbum.di.listenersDiModule
import org.greenrobot.eventbus.EventBus
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidCoreModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class App : MultiDexApplication(), KodeinAware {

    override val kodein: Kodein = Kodein.lazy {
        import(androidCoreModule(this@App))
        import(listenersDiModule())

        bind<EventBus>() with singleton { EventBus.getDefault() }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        lateinit var appInstance: App
            private set
    }
}
