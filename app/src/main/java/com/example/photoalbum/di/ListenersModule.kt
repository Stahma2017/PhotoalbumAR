package com.example.photoalbum.di

import com.example.photoalbum.data.listeners.ActivityResultListener
import com.example.photoalbum.data.listeners.PermissionsListener
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

fun listenersDiModule() = Kodein.Module("listenersDiModule") {
    bind<PermissionsListener>() with singleton { PermissionsListener() }
    bind<ActivityResultListener>() with singleton { ActivityResultListener() }
}