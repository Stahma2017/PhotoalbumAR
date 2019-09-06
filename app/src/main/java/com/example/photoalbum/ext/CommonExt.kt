package com.example.photoalbum.ext

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.Serializable
import java.util.*


const val CONTAINER_UUID = "CONTAINER_UUID"

fun FragmentActivity.closeKeyboard() {
    currentFocus?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

inline fun <reified FragmentType : Fragment, ParamType : Serializable> createFragment(param: ParamType) =
    instanceOf<FragmentType>("param" to param)

inline fun <reified T : Fragment> instanceOf(vararg params: Pair<String, Any>) = T::class.java.newInstance().apply {
    val bundle = bundleOf(*params)
    bundle.putString(CONTAINER_UUID, UUID.randomUUID().toString())
    arguments = bundle
}