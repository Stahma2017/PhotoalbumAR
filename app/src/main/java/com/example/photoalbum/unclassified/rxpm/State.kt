package com.example.photoalbum.unclassified.rxpm

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import java.util.concurrent.atomic.AtomicReference

/**
 * Reactive property for the view's state.
 * Can be observed and changed in reactive manner with it's [observable] and [consumer].
 *
 * Use to represent a view state. It can be something simple, like some widget's text, or complex,
 * like inProgress or data.
 */
class State<T>(initialValue: T? = null) {
    val relay =
        if (initialValue != null) {
            BehaviorRelay.createDefault<T>(initialValue).toSerialized()
        } else {
            BehaviorRelay.create<T>().toSerialized()
        }

    private val cachedValue =
        if (initialValue != null) {
            AtomicReference<T?>(initialValue)
        } else {
            AtomicReference()
        }

    /**
     * Observable of this [State].
     */
    val observable = relay.hide() as Observable<T>

    /**
     * Consumer of the [State].
     * Accessible only from a PresentationModel.
     *
     * Use to subscribe the state to some [Observable] source.
     */
    val consumer: Consumer<T> get() = relay

    /**
     * Returns a current value.
     * @throws UninitializedPropertyAccessException if there is no value and [State] was created without `initialValue`.
     */
    val value: T
        get() {
            return cachedValue.get() ?: throw UninitializedPropertyAccessException(
                "The State has no value yet. Use valueOrNull() or pass initialValue to the constructor."
            )
        }

    /**
     * Returns a current value or null.
     */
    val valueOrNull: T? get() = cachedValue.get()

    init {
        relay.subscribe { cachedValue.set(it) }
    }

    /**
     * Returns true if the [State] has any value.
     */
    fun hasValue() = cachedValue.get() != null
}