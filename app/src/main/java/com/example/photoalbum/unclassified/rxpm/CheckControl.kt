package com.example.photoalbum.unclassified.rxpm

import android.widget.CompoundButton
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Helps to bind a group of properties of a checkable widget to a [presentation model]PresentationModel
 * and also breaks the loop of two-way data binding to make the work with the check easier.
 *
 * You can bind this to any [CompoundButton] subclass using the familiar `bindTo` method
 *
 * Instantiate this using the [checkControl] extension function of the presentation model.
 */
class CheckControl internal constructor(initialChecked: Boolean) {
    /**
     * The checked state
     */
    val checked = State(initialChecked)

    /**
     * The checked state change events
     */
    val checkedChanges = Action<Boolean>()

    init {
        checkedChanges.relay
            .filter { it != checked.value }
            .subscribe(checked.relay)
    }
}

/**
 * Creates the [CheckControl].
 *
 * @param initialChecked initial checked state.
 */
fun checkControl(initialChecked: Boolean = false): CheckControl {
    return CheckControl(initialChecked)
}

internal inline fun CompoundButton.bind(checkControl: CheckControl): Disposable {
    return CompositeDisposable().apply {
        var editing = false
        addAll(
            checkControl.checked.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    editing = true
                    isChecked = it
                    editing = false
                },

            checkedChanges()
                .skipInitialValue()
                .filter { !editing }
                .subscribe(checkControl.checkedChanges.consumer)
        )
    }
}