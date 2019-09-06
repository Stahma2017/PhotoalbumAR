package com.example.photoalbum.unclassified.rxpm

import android.widget.EditText
import android.widget.SearchView
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.view.focusChanges
import com.jakewharton.rxbinding2.widget.queryTextChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Helps to bind a group of properties of an input field widget to a [presentation model]
 * and also breaks the loop of two-way data binding to make the work with the input easier.
 *
 * You can bind this to an [EditText] or an [TextInputLayout] using the familiar `bindTo` methods.
 *
 * Instantiate this using the [inputControl] extension function of the presentation model.
 */
class SearchViewControl internal constructor(
    initialText: String,
    initialFocused: Boolean,
    formatter: (text: String) -> String
) {
    /**
     * The input field text state
     */
    val text = State(initialText)

    /**
     * The input field text changes events
     */
    val textChanges = Action<String>()

    /**
     * The focused state
     */
    val focused = State(initialFocused)

    /**
     * The focus state changes events
     */
    val focusChanges = Action<Boolean>()

    init {
        textChanges.relay
            .filter { it != text.value }
            .map { formatter.invoke(it) }
            .subscribe {
                text.relay.accept(it)
            }
        focusChanges.relay
            .filter { it != focused.value }
            .subscribe {
                focused.relay.accept(it)
            }
    }
}

/**
 * Creates the [InputControl].
 *
 * @param initialText initial text of the input field.
 * @param initialFocus is input field initially focused.
 * @param formatter formats the user input. The default does nothing.
 */
fun seachViewControl(
    initialText: String = "",
    initialFocus: Boolean = false,
    formatter: (text: String) -> String = { it }
): SearchViewControl {
    return SearchViewControl(initialText, initialFocus, formatter)
}

internal inline fun SearchView.bind(inputControl: SearchViewControl): Disposable {
    return CompositeDisposable().apply {
        var focusing = false
        var editing = false
        addAll(
            inputControl.text.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val editable = query
                    if (!it!!.contentEquals(editable)) {
                        editing = true
                        setQuery(it, false)
                        editing = false
                    }
                },
            queryTextChanges()
                .skipInitialValue()
                .filter { !editing }
                .map { it.toString() }
                .subscribe(inputControl.textChanges.consumer),

            inputControl.focused.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    focusing = true
                    if (it) requestFocus() else clearFocus()
                    focusing = false
                },
            focusChanges()
                .skipInitialValue()
                .filter { !focusing }
                .subscribe(inputControl.focusChanges.consumer)
        )
    }
}