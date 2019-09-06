package com.example.photoalbum.app.root

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import cn.easyar.Engine
import com.example.photoalbum.App
import com.example.photoalbum.R
import com.example.photoalbum.Screens
import com.example.photoalbum.app.base.PmBaseActivity
import com.example.photoalbum.data.listeners.ActivityResult
import com.example.photoalbum.data.listeners.ActivityResultListener
import com.example.photoalbum.data.listeners.PermissionsListener
import com.example.photoalbum.data.listeners.RequestPermissionsResult
import com.example.photoalbum.eventbus.BackPressedEvent
import com.example.photoalbum.ext.strings
import com.example.photoalbum.kodeininjectionmanager.IHasRetainedKodein
import com.example.photoalbum.kodeininjectionmanager.InjectionManager
import com.example.photoalbum.notification.PushObject
import com.example.photoalbum.unclassified.ChildKodeinProvider
import com.example.photoalbum.unclassified.navigation.BackButtonListener
import com.example.photoalbum.unclassified.navigation.coordinator.Coordinator
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorEvent
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorHolder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.kodein.di.Kodein
import org.kodein.di.LateInitKodein
import org.kodein.di.LazyKodein
import org.kodein.di.generic.factory
import org.kodein.di.generic.factory2
import org.kodein.di.generic.instance
import org.kodein.di.generic.on
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

class RootView : PmBaseActivity<RootPm>(),
    IHasRetainedKodein,
    ChildKodeinProvider {

    override val layoutRes = R.layout.view_fragment_container

    //region Инъекции
    override fun providePresentationModel() = RootPm(coordinatorHolder)

    private val kodein = LateInitKodein()

    private val localCicerone: Cicerone<Router> by kodein.instance()
    private val coordinatorHolder: CoordinatorHolder by kodein.instance()

    private val eventBus: EventBus by kodein.instance()
    private val permissionsListener: PermissionsListener by kodein.instance()
    private val activityResultListener: ActivityResultListener by kodein.instance()

    lateinit var coordinator: Coordinator
    private val coordinatorFactory: ((fragmentManager: FragmentManager) -> Coordinator) by kodein.on(this).factory()

    private lateinit var navigator: RootNavigator
    private val navigatorFactory: ((activity: FragmentActivity, containerId: Int) -> RootNavigator) by kodein.factory2()
    //endregion

    //region Перегруженные методы
    override fun onCreate(savedInstanceState: Bundle?) {
        kodein.baseKodein = InjectionManager.instance.bindKodein(this)

        super.onCreate(savedInstanceState)

        navigator = navigatorFactory(this, R.id.fragmentContainer)
        coordinator = coordinatorFactory(supportFragmentManager)

        val pushJson = intent.extras?.getString(PushObject.PUSH_DATA)
        intent.extras?.remove(PushObject.PUSH_DATA)

        /*val pushModel: PushModel? =
            if (!pushJson.isNullOrBlank()) Utils.fromJson(pushJson, PushModel::class.java) else null*/

        if (Engine.initialize(this, strings[R.string.easyar_sdk_key])) {
            localCicerone.router.newRootScreen(Screens.VideoPlayback)
        }else {
            Log.e("HelloAR", "Initialization Failed.")
            Toast.makeText(this, Engine.errorMessage(), Toast.LENGTH_LONG).show()
        }


        /*pushModel?.let { Utils.processPush(localCicerone.router, it,
            isAppResumed = false,
            isHomeExistsInFragmentManager = false
        ) }*/

        coordinatorHolder.setCoordinator(coordinator)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val pushData = intent.extras?.getString(PushObject.PUSH_DATA)

        pushData?.let { this.intent.putExtra(PushObject.PUSH_DATA, it) }
    }

    override fun onPause() {
        localCicerone.navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        localCicerone.navigatorHolder.setNavigator(navigator)

        val pushData = intent.extras?.getString(PushObject.PUSH_DATA)
        intent.extras?.remove(PushObject.PUSH_DATA)

       /* val pushModel: PushModel? =
            if (!pushData.isNullOrBlank()) Utils.fromJson(pushData, PushModel::class.java) else null

        pushModel?.let { coordinator.consumeEvent(Events.ProcessPush(it)) }*/
    }

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    override fun onDestroy() {
        coordinatorHolder.removeCoordinator()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if ((fragment as? BackButtonListener)?.onBackPressed() == true) {
            return
        }
        localCicerone.router.exit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val rationaleResults = mutableListOf<Boolean>()
        permissions.forEachIndexed { index, s ->
            rationaleResults.add(
                if (grantResults[index] == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.shouldShowRequestPermissionRationale(this, s)
                else false
            )
        }

        permissionsListener.sendResult(
            requestCode,
            RequestPermissionsResult(
                permissions,
                grantResults,
                rationaleResults.toBooleanArray()
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        activityResultListener.sendResult(
            requestCode,
            ActivityResult(resultCode, data)
        )
    }

    override fun getRetainedKodein(): Kodein = LazyKodein {
        Kodein {
            extend(App.appInstance.kodein)
            import(rootContainerDiModule(), allowOverride = true)
        }
    }

    override fun getKodeinKey() = "RootView"

    override fun getChildKodein(): Kodein = kodein
    //endregion

    @Subscribe
    fun onEvent(event: BackPressedEvent) = onBackPressed()

    sealed class Events : CoordinatorEvent() {
        //data class ProcessPush(val pushModel: PushModel) : Events()
    }
}