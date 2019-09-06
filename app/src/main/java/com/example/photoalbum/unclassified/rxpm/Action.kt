package com.example.photoalbum.unclassified.rxpm

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * Reactive property for the actions from the view
 * Can be changed and observed in reactive manner with it's [consumer] and [observable].
 *
 * Use to send actions of the view, e.g. some widget's clicks.
 */
class Action<T> {
    val relay = PublishRelay.create<T>().toSerialized()

    /**
     * Consumer of the [Action][Action].
     */
    val consumer get() = relay as Consumer<T>

    /**
     * Observable of the [Action].
     * Accessible only from a PresentationModel.
     *
     * Use to subscribe to this [Action]s source.
     */
    val observable: Observable<T> get() = relay
}