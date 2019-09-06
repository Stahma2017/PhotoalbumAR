package com.example.photoalbum.app.videoPlayback

import com.example.photoalbum.app.base.BasePresentationModel
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorEvent
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorRouter

class VideoPlaybackPm(private val coordinatorRouter: CoordinatorRouter) : BasePresentationModel() {

    override fun onCreate() {


    }

    sealed class Events : CoordinatorEvent() {

    }
}