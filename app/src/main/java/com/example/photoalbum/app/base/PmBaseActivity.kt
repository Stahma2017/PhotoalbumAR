package com.example.photoalbum.app.base

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.SearchView
import android.widget.Toast
import me.dmdev.rxpm.base.PmSupportActivity

abstract class PmBaseActivity<T : BasePresentationModel> : PmSupportActivity<T>() {
    abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        prepareUi()
    }

    open fun prepareUi() = Unit

    override fun onBindPresentationModel(pm: T) {
        // --- States ---
        pm.showMsgByIdCommand.observable bindTo { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        pm.showMsgCommand.observable bindTo { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
    }


    //Delete if not used
  /*  //Local extension to bind the [InputControl] to the [TextInputLayout][textInputLayout], use it ONLY in [onViewCreated].
    infix fun InputControl.bindTo(textInputLayout: TextInputLayout) {
        compositeUnbind.add(textInputLayout.bind(this))
    }

    //Local extension to bind the [CheckControl] to the [CompoundButton][compoundButton], use it ONLY in [onViewCreated].
    infix fun CheckControl.bindTo(compoundButton: CompoundButton) {
        compositeUnbind.add(compoundButton.bind(this))
    }

    infix fun SearchViewControl.bindTo(searchView: SearchView) {
        compositeUnbind.add(searchView.bind(this))
    }*/

}