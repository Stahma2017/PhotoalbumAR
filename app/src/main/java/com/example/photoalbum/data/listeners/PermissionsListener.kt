package com.example.photoalbum.data.listeners

import android.util.SparseArray

class PermissionsListener {
    private val listeners = SparseArray<(RequestPermissionsResult) -> Unit>()

    fun sendResult(code: Int, model: RequestPermissionsResult) = listeners[code]?.invoke(model)

    fun addListener(code: Int, selectionListener: ((RequestPermissionsResult) -> Unit)) =
        listeners.put(code, selectionListener)

    fun removeListener(code: Int) = listeners.remove(code)
}

data class RequestPermissionsResult(
    val permissions: Array<String>,
    val grantResults: IntArray,
    val shouldShowRequestPermissionRationale: BooleanArray
)