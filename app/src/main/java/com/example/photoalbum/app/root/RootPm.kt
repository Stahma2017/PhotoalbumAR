package com.example.photoalbum.app.root

import com.example.photoalbum.app.base.BasePresentationModel
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorRouter

class RootPm(private val coordinatorRouter: CoordinatorRouter) : BasePresentationModel() {

    override fun onCreate() {
       // eventBus.register(this)
    }

    override fun onDestroy() {
     //   eventBus.unregister(this)
        super.onDestroy()
    }

}