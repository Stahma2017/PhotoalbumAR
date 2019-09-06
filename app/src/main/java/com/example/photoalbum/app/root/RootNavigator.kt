package com.example.photoalbum.app.root

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import com.example.photoalbum.Screens
import com.example.photoalbum.ext.closeKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.browse
import ru.terrakok.cicerone.Screen
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

class RootNavigator(private val activity: FragmentActivity, @IdRes private val containerId: Int) :
    SupportAppNavigator(activity, containerId) {
    override fun applyCommands(commands: Array<out Command>) {
        GlobalScope.launch(Dispatchers.Main) {
            activity.closeKeyboard()
            super.applyCommands(commands)
        }
    }

    override fun activityBack() {
        activity.moveTaskToBack(true)
    }

    override fun applyCommand(command: Command) {
        var screen: Screen? = null
        when (command) {
            is Forward -> {
                screen = command.screen
                when (screen) {
                    is Screens.WebBrowser -> {
                        activity.browse((command.screen as Screens.WebBrowser).site)
                        return
                    }
                }
            }

            is Replace -> screen = command.screen
        }
        super.applyCommand(command)
    }

    override fun fragmentForward(command: Forward) {
        val screen = command.screen as SupportAppScreen
        /*if (screen.fragment is HomeView) {
            val fragment = createFragment(screen)

            val fragmentTransaction = activity.supportFragmentManager.beginTransaction()

            fragmentTransaction
                .replace(containerId, fragment, HomeView.tag())
                .addToBackStack(screen.screenKey)
                .commit()
            return
        }*/
        super.fragmentForward(command)
    }

    override fun fragmentReplace(command: Replace) {
        val screen = command.screen as SupportAppScreen
        /*if (screen.fragment is HomeView) {
            val fragment = createFragment(screen)

            if (activity.supportFragmentManager.backStackEntryCount > 0) {
                activity.supportFragmentManager.popBackStack()

                activity.supportFragmentManager.beginTransaction()
                    .replace(containerId, fragment, HomeView.tag())
                    .addToBackStack(screen.screenKey)
                    .commit()
            } else {
                activity.supportFragmentManager.beginTransaction()
                    .replace(containerId, fragment, HomeView.tag())
                    .commit()
            }
            return
        }*/
        super.fragmentReplace(command)
    }
}