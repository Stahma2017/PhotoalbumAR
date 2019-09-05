package com.example.photoalbum.kodeininjectionmanager.helpers

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photoalbum.kodeininjectionmanager.IHasRetainedKodein
import com.example.photoalbum.kodeininjectionmanager.KodeinsStore

internal class ActivityLifecycleHelper(private val kodeinsStore: KodeinsStore) :
    Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is IHasRetainedKodein && activity.isFinishing) {
            kodeinsStore.remove(activity.getKodeinKey())
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivityCreated(activity: Activity, outState: Bundle?) {
        (activity as? AppCompatActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
            FragmentLifecycleHelper(kodeinsStore),
            true
        )
    }
}