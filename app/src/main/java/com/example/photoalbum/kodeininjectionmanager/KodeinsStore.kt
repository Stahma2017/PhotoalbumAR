package com.example.photoalbum.kodeininjectionmanager

import com.example.photoalbum.kodeininjectionmanager.exceptions.KodeinNotFoundException
import org.kodein.di.Kodein

internal class KodeinsStore {
    private val kodeinsForView = mutableMapOf<String, Kodein>()

    fun isExist(key: String): Boolean = kodeinsForView.containsKey(key)

    fun add(key: String, component: Kodein) {
        kodeinsForView[key] = component
    }

    fun get(key: String): Kodein = kodeinsForView[key] ?: throw KodeinNotFoundException()

    fun remove(key: String) {
        kodeinsForView.remove(key)
    }
}