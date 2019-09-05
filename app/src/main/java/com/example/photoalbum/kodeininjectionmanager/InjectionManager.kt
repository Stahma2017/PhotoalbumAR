package com.example.photoalbum.kodeininjectionmanager

import android.app.Application
import android.util.Log
import com.example.photoalbum.kodeininjectionmanager.helpers.ActivityLifecycleHelper
import org.kodein.di.Kodein

class InjectionManager {
    companion object {
        @JvmStatic
        val instance = InjectionManager()
    }

    private val kodeinStore = KodeinsStore()

    fun init(app: Application) = app.registerActivityLifecycleCallbacks(
        ActivityLifecycleHelper(kodeinStore)
    )

    @Suppress("UNCHECKED_CAST")
    fun bindKodein(owner: IHasRetainedKodein): Kodein = getKodeinOrCreate(owner.getKodeinKey(), owner)

    private fun getKodeinOrCreate(key: String, owner: IHasRetainedKodein): Kodein {
        return when {
            kodeinStore.isExist(key) -> {
                Log.d("InjectionManager", "Kodein for key=$key was found in cache. Getting from cache")
                kodeinStore.get(key)
            }
            else -> {
                Log.d("InjectionManager", "Kodein for key=$key not found in cache. Creating new one")
                owner.getRetainedKodein().also { kodeinStore.add(key, it) }
            }
        }
    }
}