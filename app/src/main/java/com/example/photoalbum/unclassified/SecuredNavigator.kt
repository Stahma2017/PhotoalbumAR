package com.example.photoalbum.unclassified

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.photoalbum.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.kodein.di.KodeinAware
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

open class SecuredNavigator : SupportAppNavigator, KodeinAware {
    override val kodein by lazy { App.appInstance.kodein }

    protected val eventBus: EventBus = EventBus.getDefault()

    val onExit: (() -> Unit)?

    constructor(activity: FragmentActivity, containerId: Int, onExit: (() -> Unit)?) : super(activity, containerId) {
        this.onExit = onExit
    }

    constructor(
        activity: FragmentActivity,
        fragmentManager: FragmentManager,
        containerId: Int,
        onExit: (() -> Unit)?
    ) : super(activity, fragmentManager, containerId) {
        this.onExit = onExit
    }

    override fun activityBack() = onExit?.invoke() ?: super.activityBack()

    override fun applyCommands(commands: Array<out Command>) {
        GlobalScope.launch(Dispatchers.Main) { super.applyCommands(commands) }
    }
}