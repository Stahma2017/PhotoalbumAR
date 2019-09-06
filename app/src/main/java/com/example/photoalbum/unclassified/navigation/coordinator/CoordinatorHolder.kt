package com.example.photoalbum.unclassified.navigation.coordinator

import java.util.*

class CoordinatorHolder : CoordinatorRouter {
    private var coordinator: Coordinator? = null
    private val pendingEvents = LinkedList<CoordinatorEvent>()

    fun setCoordinator(coordinator: Coordinator?) {
        this.coordinator = coordinator
        while (!pendingEvents.isEmpty()) {
            if (coordinator != null) {
                sendEvent(pendingEvents.poll())
            } else {
                break
            }
        }
    }

    fun removeCoordinator() {
        coordinator = null
    }

    override fun sendEvent(event: CoordinatorEvent) {
        if (coordinator != null) {
            coordinator!!.consumeEvent(event)
        } else {
            pendingEvents.add(event)
        }
    }
}