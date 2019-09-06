package com.example.photoalbum.app.root

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.photoalbum.unclassified.SecuredNavigator
import com.example.photoalbum.unclassified.navigation.coordinator.Coordinator
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorHolder
import com.example.photoalbum.unclassified.navigation.coordinator.StubCoordinator
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.*
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

fun rootContainerDiModule() = Kodein.Module("rootContainerDiModule") {
    bind<Cicerone<Router>>() with singleton { Cicerone.create() }
    bind<CoordinatorHolder>() with singleton { CoordinatorHolder() }

    bind<Coordinator>() with scoped(AndroidLifecycleScope.multiItem).multiton { fragmentManager: FragmentManager ->
        RootCoordinator(
            instance<Cicerone<Router>>().router,
            instance(),
            fragmentManager)
    }

    bind<Coordinator>() with singleton { StubCoordinator() }

    bind<SecuredNavigator>() with factory { activity: FragmentActivity, fragmentManager: FragmentManager, containerId: Int, onExit: () -> Unit ->
        SecuredNavigator(activity, fragmentManager, containerId, onExit)
    }

    bind<RootNavigator>() with factory { activity: FragmentActivity, resId: Int ->
        RootNavigator(activity, resId)
    }
}