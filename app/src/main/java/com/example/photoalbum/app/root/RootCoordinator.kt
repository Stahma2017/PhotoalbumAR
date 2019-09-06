package com.example.photoalbum.app.root

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.photoalbum.Screens
import com.example.photoalbum.app.videoPlayback.VideoPlaybackPm
import com.example.photoalbum.unclassified.navigation.coordinator.Coordinator
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorEvent
import ru.terrakok.cicerone.Router

class RootCoordinator(
    private val navigationRouter: Router,
    private val context: Context,
    private val fragmentManager: FragmentManager
) : Coordinator {

    override fun consumeEvent(event: CoordinatorEvent) {
        when (event){


        }
    }
}
