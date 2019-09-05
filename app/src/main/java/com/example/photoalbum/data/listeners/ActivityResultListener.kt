package com.example.photoalbum.data.listeners

import android.content.Intent
import android.util.SparseArray

class ActivityResultListener {
    private val listeners = SparseArray<(ActivityResult) -> Unit>()

    fun sendResult(code: Int, model: ActivityResult) = listeners[code]?.invoke(model)

    fun addListener(code: Int, selectionListener: ((ActivityResult) -> Unit)) = listeners.put(code, selectionListener)

    fun removeListener(code: Int) = listeners.remove(code)
}

data class ActivityResult(val resultCode: Int, val data: Intent?)