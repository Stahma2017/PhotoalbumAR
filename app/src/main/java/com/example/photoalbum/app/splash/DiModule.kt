package com.example.photoalbum.app.splash

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.photoalbum.unclassified.SecuredNavigator
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.factory
import org.kodein.di.generic.singleton
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

fun splashContainerDiModule() = Kodein.Module("splashContainerDiModule") {
    bind<Cicerone<Router>>() with singleton { Cicerone.create() }

    bind<SecuredNavigator>() with factory { activity: FragmentActivity, fragmentManager: FragmentManager, containerId: Int, onExit: () -> Unit ->
        SecuredNavigator(activity, fragmentManager, containerId, onExit)
    }
}