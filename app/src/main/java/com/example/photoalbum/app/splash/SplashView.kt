package com.example.photoalbum.app.splash

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.example.photoalbum.App
import com.example.photoalbum.R
import com.example.photoalbum.app.base.PmBaseActivity
import com.example.photoalbum.kodeininjectionmanager.IHasRetainedKodein
import com.example.photoalbum.kodeininjectionmanager.InjectionManager
import com.example.photoalbum.notification.PushObject
import com.example.photoalbum.unclassified.SecuredNavigator
import org.kodein.di.Kodein
import org.kodein.di.LateInitKodein
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Command

class SplashView : PmBaseActivity<SplashPm>(), IHasRetainedKodein {

    override fun providePresentationModel(): SplashPm =
        SplashPm(
            localCicerone.router,
            intent.extras?.getString(PushObject.PUSH_DATA)
        )

    private val kodein = LateInitKodein()

    private val localCicerone: Cicerone<Router> by kodein.instance()

    override val layoutRes = R.layout.view_splash

    private val navigator: SecuredNavigator by lazy {
        object : SecuredNavigator(this@SplashView, 50, null) {
            override fun createStartActivityOptions(command: Command, activityIntent: Intent): Bundle {
                val options = ActivityOptions.makeCustomAnimation(this@SplashView, 0, 0)
                return options.toBundle()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        kodein.baseKodein = InjectionManager.instance.bindKodein(this)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onPause() {
        localCicerone.navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        localCicerone.navigatorHolder.setNavigator(navigator)
    }

    override fun getRetainedKodein(): Kodein = LazyKodein {
        Kodein {
            extend(App.appInstance.kodein)
            import(splashContainerDiModule(), allowOverride = true)
        }
    }

    override fun getKodeinKey() = "SplashView"
}
