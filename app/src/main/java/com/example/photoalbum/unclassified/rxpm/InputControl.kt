package com.example.photoalbum.unclassified.rxpm

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import com.example.photoalbum.data.models.interfaces.Maskable
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.view.focusChanges
import com.jakewharton.rxbinding2.widget.textChanges
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
class InputControl internal constructor(
    initialText: String,
    initialFocused: Boolean,
    var formatter: Maskable?
) {
    private val hideErrorOnUserInput: Boolean = true
    private val showErrorOnFocusLost: Boolean = true

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

    /**
     * The input field error state
     */
    val error = State<String>()

    /**
     * The input field error state
     */
    val hiddenError = State<String>()

    init {
        textChanges.relay
            .filter { it != text.value }
            .subscribe {
                text.relay.accept(it)
                if (hideErrorOnUserInput && !error.valueOrNull.isNullOrBlank()) error.relay.accept("")
            }
        focusChanges.relay
            .filter { it != focused.value }
            .subscribe {
                focused.relay.accept(it)
                if (showErrorOnFocusLost && !it && !hiddenError.valueOrNull.isNullOrBlank()) error.relay.accept(
                    hiddenError.valueOrNull
                )
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
fun inputControl(
    initialText: String = "",
    initialFocus: Boolean = false,
    formatter: Maskable? = null
): InputControl {
    return InputControl(initialText, initialFocus, formatter)
}

internal inline fun TextInputLayout.bind(inputControl: InputControl): Disposable {
    val edit = editText!!

    return io.reactivex.disposables.CompositeDisposable().apply {
        addAll(
            edit.bind(inputControl),
            inputControl.error.observable.subscribe { error -> this@bind.error = if (error.isEmpty()) null else error }
        )
    }
}

internal inline fun EditText.bind(inputControl: InputControl): Disposable {
    return CompositeDisposable().apply {
        var focusing = false
        var editing = false
        addAll(
            inputControl.text.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (inputControl.formatter != null && it != inputControl.formatter!!.getRawText()) {
                        editing = true
                        setText(it)
                        editing = false
                    } else if (inputControl.formatter == null) {
                        val editable = text
                        if (!it!!.contentEquals(editable)) {
                            editing = true
                            if (editable is Spanned) {
                                val ss = SpannableString(it)
                                TextUtils.copySpansFrom(editable, 0, ss.length, null, ss, 0)
                                editable.replace(0, editable.length, ss)
                            } else {
                                editable.replace(0, editable.length, it)
                            }
                            editing = false
                        }
                    }
                },
            textChanges()
                .skipInitialValue()
                .filter {
                    if (inputControl.formatter == null) {
                        return@filter !editing
                    } else {
                        return@filter !editing && inputControl.formatter!!.getMaskedText() == it.toString()
                    }
                }
                .map {
                    if (inputControl.formatter == null) {
                        return@map it.toString()
                    } else {
                        return@map inputControl.formatter!!.getRawText()
                    }
                }
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