package com.example.photoalbum.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object Utils {

    fun createAlertDialog(context: Context, title: String, message: String, textOk: String, textNegaive: String, okListener: DialogInterface.OnClickListener, negativeListener: DialogInterface.OnClickListener): AlertDialog? =
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(textOk, okListener)
            .setNegativeButton(textNegaive, negativeListener).create()
}