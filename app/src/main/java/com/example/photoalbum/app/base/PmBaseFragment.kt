package com.example.photoalbum.app.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.photoalbum.App
import com.example.photoalbum.ext.ContextAware
import com.example.photoalbum.unclassified.ChildKodeinProvider
import me.dmdev.rxpm.base.PmSupportFragment
import org.kodein.di.Kodein

abstract class PmBaseFragment<T : BasePresentationModel> : PmSupportFragment<T>(),
    ContextAware {
    private var progressDialog: ProgressDialog? = null

    abstract val layoutRes: Int
   // protected open val statusBarId: Int? = R.color.blue14
    private var originalInputMode: Int? = null
    protected open val inputMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        originalInputMode = activity?.window?.attributes?.softInputMode
        inputMode?.let { activity?.window?.setSoftInputMode(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (originalInputMode != null && inputMode != null) activity?.window?.setSoftInputMode(originalInputMode!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let { processArguments(it) }

        val view = inflater.inflate(layoutRes, container, false)
        prepareUi(view, savedInstanceState)

        return view
    }

    /*override fun onResume() {
        statusBarId?.let { setStatusBarColor(colors[it]) }
        super.onResume()
    }*/

    override fun onPause() {
        hideProgressDialog()
        super.onPause()
    }

    open fun processArguments(arguments: Bundle) = Unit

    open fun prepareUi(view: View, savedInstanceState: Bundle?) {
        val styledAttributes = context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
    }

    override fun onBindPresentationModel(pm: T) {
        // --- States ---
        pm.showMsgByIdCommand.observable bindTo {
            // Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            AlertDialog.Builder(context)
                .setMessage(it)
                .setNegativeButton("ОК") { dialog, _ ->  dialog.cancel() }
                .show()
        }
        pm.showMsgCommand.observable bindTo {
            AlertDialog.Builder(context)
                .setMessage(it)
                .setNegativeButton("ОК") { dialog, _ ->  dialog.cancel() }
                .show()}

        pm.requestPermissionCommand.observable bindTo {
            ActivityCompat.requestPermissions(activity!!, it.permissions, it.requestCode)
        }
    }

    override fun getContext(): Context = super.getContext()!!

   /* //Local extension to bind the [InputControl] to the [TextInputLayout][textInputLayout], use it ONLY in [onViewCreated].
    infix fun InputControl.bindTo(textInputLayout: TextInputLayout) {
        compositeUnbind.add(textInputLayout.bind(this))
    }

    //Local extension to bind the [InputControl] to the [EditText][editText], use it ONLY in [onViewCreated].
    infix fun InputControl.bindTo(editText: EditText) {
        compositeUnbind.add(editText.bind(this))
    }

    //Local extension to bind the [CheckControl] to the [CompoundButton][compoundButton], use it ONLY in [onViewCreated].
    infix fun CheckControl.bindTo(compoundButton: CompoundButton) {
        compositeUnbind.add(compoundButton.bind(this))
    }

    infix fun SearchViewControl.bindTo(searchView: SearchView) {
        compositeUnbind.add(searchView.bind(this))
    }*/

    protected fun showProgressDialog(msg: String) {
        hideProgressDialog()

        progressDialog = ProgressDialog(this.context)
        progressDialog!!.setOnCancelListener { presentationModel.stopProcessAction.consumer.accept(Unit) }

        progressDialog!!.setMessage(msg)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
    }

    protected fun hideProgressDialog() {
        progressDialog?.run { if (isShowing) dismiss() }
        progressDialog = null
    }

    protected fun getClosestParentKodein(): Kodein = when {
        parentFragment is ChildKodeinProvider -> (parentFragment!! as ChildKodeinProvider).getChildKodein()
        activity is ChildKodeinProvider -> (activity!! as ChildKodeinProvider).getChildKodein()
        else -> App.appInstance.kodein
    }
}